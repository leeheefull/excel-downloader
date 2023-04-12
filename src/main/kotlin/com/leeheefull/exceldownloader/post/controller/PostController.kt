package com.leeheefull.exceldownloader.post.controller

import com.leeheefull.exceldownloader.excel.ExcelResponseHelper
import com.leeheefull.exceldownloader.post.controller.dto.response.PostResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/posts"])
class PostController(
    private val excelResponseHelper: ExcelResponseHelper
) {
    @GetMapping(value = ["/v1"])
    fun getPosts(): List<PostResponse> {
        return postsResponse
    }

    @GetMapping(value = ["/v1/excel"])
    fun getPostsExcelDownload(
        response: HttpServletResponse,
    ) {
        val fileName = "글 목록 조회"
        val excelResult = postsResponse

        excelResponseHelper.write(response, fileName, excelResult)
    }
}

private val postsResponse = listOf(
    PostResponse(
        id = 1,
        title = "kotlin",
        content = "정말 재밌다.",
        author = "이희찬",
    ),
    PostResponse(
        id = 2,
        title = "java",
        content = "재밌다.",
        author = "이희찬",
    ),
    PostResponse(
        id = 3,
        title = "node",
        content = "재미없다.",
        author = "이희찬",
    ),
    PostResponse(
        id = 4,
        title = "php",
        content = "롸?",
        author = "이희찬",
    )
)
