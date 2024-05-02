package com.kltn.anigan.domain.response

import com.kltn.anigan.domain.ImageClassFromInternet

data class TransformResponse(
    val statusCode: Int,
    val message: String,
    val data: ImageClassFromInternet
)