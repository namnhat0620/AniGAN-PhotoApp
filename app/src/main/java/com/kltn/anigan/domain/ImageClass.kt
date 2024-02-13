package com.kltn.anigan.domain

import com.kltn.anigan.R
import java.net.URL

data class ImageClass(
    val id: Int = 1,
    val imageId: Int = R.drawable.default_image,
    val description: String = ""
)

data class ImageClassFromInternet(
    val image_id: Int,
    val url: URL,
    val type: Int
)