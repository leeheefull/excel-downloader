package com.leeheefull.exceldownloader.excel

import com.leeheefull.exceldownloader.excel.ExcelConstants.COLUMN_START_INDEX
import com.leeheefull.exceldownloader.excel.ExcelConstants.DEFAULT_SHEET_NAME
import com.leeheefull.exceldownloader.excel.ExcelConstants.ROW_START_INDEX
import java.io.IOException
import java.io.OutputStream
import java.lang.reflect.Field
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Component

@Component
class ExcelFileConverter {
    /**
     * excel file 변환 함수
     *
     * @param data excel row
     * @param stream 출력 stream
     * @param sheetName excel sheet 이름
     */
    @Throws(IOException::class)
    fun write(
        data: List<Any>,
        stream: OutputStream,
        sheetName: String = DEFAULT_SHEET_NAME,
    ) {
        validateMaxRow(data)
        SXSSFWorkbook()  // SXSSFWorkbook instance 생성
            .apply { setCompressTempFiles(true) }   // 임시 파일을 압축 설정
            .render(data, sheetName)    // "data"를 "sheetName"으로 지정된 시트에 작성
            .apply { write(stream) }    // 생성된 엑셀 파일을 stream 출력 스트림으로
            .dispose()  // 자원 정리
        stream.close()  // stream 종료
    }

    /**
     * "Excel"의 최대 행 수를 초과하는지 검증
     *
     * @param data excel row
     */
    private fun validateMaxRow(data: List<Any>) {
        val maxRows: Int = SpreadsheetVersion.EXCEL2007.maxRows
        require(data.size <= maxRows) {
            String.format(
                "This concrete ExcelFile does not support over %s rows",
                maxRows
            )
        }
    }

    /**
     * SXSSFWorkbook 객체에 데이터를 기반으로 Excel 시트를 생성하는 함수
     *
     * @param data 랜더링할 객체
     * @param sheetName 엑셀 시트 이름
     * @return SXSSFWorkbook 객체
     */
    private fun SXSSFWorkbook.render(
        data: List<Any>,
        sheetName: String,
    ): SXSSFWorkbook {
        val sheet = this.createSheet(sheetName)

        if (data.isNotEmpty()) {
            var rowIndex = ROW_START_INDEX

            sheet.renderHeaders(data[0], rowIndex++)
            data.forEach { renderedData ->
                val fields = renderedData::class.java.declaredFields
                    .asSequence()
                    .filter { !it.type.name.lowercase().contains("companion") }
                sheet.renderRow(fields, renderedData, rowIndex++)
            }
        }
        return this
    }

    /**
     * Apache POI 라이브러리를 사용하여 Excel 시트의 header를 렌더링하는 함수
     *
     * @param data header를 렌더링하는 데 사용될 객체
     * @param rowIndex header가 렌더링될 행의 인덱스
     * @param columnIndex header가 렌더링될 첫 번째 열의 인덱스
     */
    private fun Sheet.renderHeaders(
        data: Any,
        rowIndex: Int = ROW_START_INDEX,
        columnIndex: Int = COLUMN_START_INDEX,
    ) {
        val row: Row = this.createRow(rowIndex)
        var currentColumnIndex = columnIndex
        val headers = getHeaders(data)

        headers.forEach {
            row.createCell(currentColumnIndex++)
                .setCellValue(it)
        }
    }

    /**
     * Sheet에 데이터를 렌더링하는 함수
     *
     * @param fields 해당 객체의 필드들을 나타내는 Sequence<Field>
     * @param data 실제 렌더링할 객체
     * @param rowIndex 어느 행에 데이터를 렌더링할지 결정
     * @param columnStartIndex 해당 행의 어느 열부터 데이터를 렌더링할지 결정
     */
    private fun Sheet.renderRow(
        fields: Sequence<Field>,
        data: Any,
        rowIndex: Int,
        columnStartIndex: Int = COLUMN_START_INDEX,
    ) {
        val row: Row = this.createRow(rowIndex)
        var columnIndex = columnStartIndex

        fields.filter { it.isAnnotationPresent(ExcelColumn::class.java) }
            .forEach { field ->
                val cell: Cell = row.createCell(columnIndex++)
                try {
                    field.isAccessible = true
                    renderCell(cell, field.get(data))
                } catch (e: Exception) {
                    throw RuntimeException("Failure to render body data: ${e.message}")
                }
            }
    }

    /**
     * 엑셀 셀에 값을 렌더링(render)하는 함수
     *
     * @param cell 파라미터는 값을 렌더링할 셀을 가리키는 객체
     * @param cellValue 해당 셀에 렌더링할 값
     */
    private fun renderCell(cell: Cell, cellValue: Any?) {
        if (cellValue is Number) {
            cell.setCellValue(cellValue.toDouble())
        } else {
            val value = cellValue?.toString() ?: ""
            cell.setCellValue(value)
        }
    }

    /**
     * 객체의 필드들 중에서 @ExcelColumn 어노테이션이 있는 필드들의 헤더명을 추출하는 함수
     *
     * @param target @ExcelColumn 어노테이션이 있는 객체
     * @return 추출된 헤더 정보
     */
    private fun getHeaders(target: Any): Set<String> {
        return target::class.java.declaredFields
            .asSequence()
            .filter { it.isAnnotationPresent(ExcelColumn::class.java) }
            .map { it.getAnnotation(ExcelColumn::class.java).headerName }
            .toSet()
    }
}
