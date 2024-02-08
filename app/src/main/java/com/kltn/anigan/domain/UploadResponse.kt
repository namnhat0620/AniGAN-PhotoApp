package com.kltn.anigan.domain

data class UploadResponse(

    val error: Boolean,
    val message: String,
    val filename: String,
)