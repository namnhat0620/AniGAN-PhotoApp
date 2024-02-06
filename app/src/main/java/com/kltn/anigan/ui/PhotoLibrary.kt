package com.kltn.anigan.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kltn.anigan.ImageClass

@Composable
internal fun PhotoLibrary(itemList: List<ImageClass>, modifier: Modifier = Modifier) {
    var chosenItemIndex: Int by remember { mutableIntStateOf(0) }
    LazyRow (
        modifier = modifier
            .padding(vertical = 15.dp)
    ){
        items(itemList, key = { it.id }) {
            Image(
                painter = painterResource(id = it.imageId),
                contentDescription = it.description,
                modifier
                    .padding(start = 12.dp)
                    .size(100.dp)
                    .clickable {
                        chosenItemIndex = it.id
                    }
                    .graphicsLayer { alpha = if (it.id == chosenItemIndex) 0.5f else 1f }
            )
        }

    }

}