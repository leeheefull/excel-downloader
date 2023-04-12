package com.leeheefull.exceldownloader.excel

import com.leeheefull.exceldownloader.excel.ExcelConstants.DEFAULT_FILE_NAME
import com.leeheefull.exceldownloader.excel.ExcelConstants.CONTENT_TYPE
import com.leeheefull.exceldownloader.excel.ExcelConstants.HEADER_NAME
import jakarta.servlet.http.HttpServletResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.springframework.stereotype.Component

@Component
class ExcelResponseHelper(
    private val excelFileConverter: ExcelFileConverter
) {
    fun write(
        response: HttpServletResponse,
        fileName: String = DEFAULT_FILE_NAME,
        excelResults: List<Any> = emptyList(),
    ) {
        val encodingFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        response.setHeader(HEADER_NAME, "attachment;filename=\"${encodingFileName}.xlsx\"")
        response.contentType = CONTENT_TYPE

        excelFileConverter.write(excelResults, response.outputStream)
    }
}
