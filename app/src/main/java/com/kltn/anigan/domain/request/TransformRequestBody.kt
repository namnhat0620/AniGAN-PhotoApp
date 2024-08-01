package com.kltn.anigan.domain.request

import com.google.gson.annotations.SerializedName
import com.kltn.anigan.domain.enums.ResolutionOption

data class TransformRequest(
    @SerializedName("source_img") val sourceImg: String,
    @SerializedName("reference_img") val referenceId: Int,
    @SerializedName("mobile_id") val mobileId: String,
    @SerializedName("resolution_option") val resolutionOption: String,
)
