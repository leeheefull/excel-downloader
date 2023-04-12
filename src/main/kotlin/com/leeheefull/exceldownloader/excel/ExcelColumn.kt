package com.leeheefull.exceldownloader.excel

import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.FIELD

@Target(FIELD)
@Retention(RUNTIME)
annotation class ExcelColumn(
    val headerName: String = "",
)
