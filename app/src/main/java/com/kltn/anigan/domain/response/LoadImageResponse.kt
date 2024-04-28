package com.kltn.anigan.domain.response

import com.kltn.anigan.domain.ImageClassFromInternet

data class LoadImageResponse (
    val statusCode: Int,
    val message: String,
    val data: PaginationResponse
)

data class PaginationResponse(
    val limit: Int,
    val total_record: Int,
    val list: List<ImageClassFromInternet>
)