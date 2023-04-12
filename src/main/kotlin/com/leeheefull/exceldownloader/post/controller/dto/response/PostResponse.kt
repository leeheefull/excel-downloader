package com.leeheefull.exceldownloader.post.controller.dto.response

import com.leeheefull.exceldownloader.excel.ExcelColumn

data class PostResponse(
    @ExcelColumn(headerName = "No")
    val id: Long,

    @ExcelColumn(headerName = "제목")
    val title: String,

    @ExcelColumn(headerName = "내용")
    val content: String,

    @ExcelColumn(headerName = "작성자")
    val author: String,
)
