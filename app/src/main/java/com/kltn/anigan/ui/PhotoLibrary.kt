package com.kltn.anigan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kltn.anigan.ImageClass

@Composable
internal fun PhotoLibrary(itemList: List<ImageClass>, modifier: Modifier = Modifier) {
    LazyRow (
        modifier = modifier
            .padding(vertical = 15.dp)
    ){
        items(itemList) {
            Image(
                painter = painterResource(id = it.imageId),
                contentDescription = it.description,
                modifier
                    .padding(start = 12.dp)
                    .size(100.dp)
            )
        }

    }

}