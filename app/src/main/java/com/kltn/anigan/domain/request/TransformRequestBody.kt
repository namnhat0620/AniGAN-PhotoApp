package com.kltn.anigan.domain.request

import com.google.gson.annotations.SerializedName

data class TransformRequest(
    @SerializedName("source_img") val sourceImg: String
)
