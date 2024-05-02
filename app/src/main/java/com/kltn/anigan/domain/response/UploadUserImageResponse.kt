package com.kltn.anigan.domain.response

data class UploadUserImageResponse(
    val image_id: Int,
    val url: String,
    val type: Int
)