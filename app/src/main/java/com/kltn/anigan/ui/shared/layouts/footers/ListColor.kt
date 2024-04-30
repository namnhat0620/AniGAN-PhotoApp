package com.kltn.anigan.ui.shared.layouts.footers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ListColor(onClick: (Color) -> Unit) {
    val listColor = arrayListOf(
        Color.Red,
        Color.Blue,
        Color.Cyan,
        Color.Green,
        Color.Gray,
        Color.Yellow,
        Color.Magenta,
        Color.White
    )

    Row(
        modifier = Modifier.horizontalScroll(state = rememberScrollState(), enabled = true)
    ) {
        listColor.forEach {
            Box(
                Modifier
                    .background(it)
                    .width(60.dp)
                    .height(60.dp)
                    .clickable { onClick(it) }) {}
        }
    }
}
