package com.kltn.anigan.ui.shared.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.kltn.anigan.R
import com.kltn.anigan.domain.ImageClassFromInternet

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun PhotoLibrary(
    itemList: List<ImageClassFromInternet>,
    modifier: Modifier = Modifier,
    setReferenceImageUrl: ((String) -> Unit)? = null,
    ) {
    var chosenItemIndex: Int by remember { mutableIntStateOf(0) }
    LazyRow (
        modifier = modifier
            .padding(vertical = 15.dp)
    ){
        items(itemList, key = { it.image_id }) {
            GlideImage(
                model = it.url,
                failure = placeholder(R.drawable.default_image),
                contentDescription = null,
                modifier = modifier
                    .padding(start = 12.dp)
                    .size(100.dp)
                    .clickable {
                        chosenItemIndex = it.image_id
                        if (setReferenceImageUrl != null) {
                            setReferenceImageUrl(it.url.toString())
                        }
                    }
                    .graphicsLayer { alpha = if (it.image_id == chosenItemIndex) 0.5f else 1f }
            )
        }

    }

}