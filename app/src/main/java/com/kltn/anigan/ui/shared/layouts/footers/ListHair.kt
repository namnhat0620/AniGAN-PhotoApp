package com.kltn.anigan.ui.shared.layouts.footers

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kltn.anigan.R


@Composable
fun ListHair(onClick: (Int) -> Unit) {
    val listColor = arrayListOf(
        R.drawable.male_3_1,
        R.drawable.female_6_1,
        R.drawable.female_7_1,
        R.drawable.female_8_1,
        R.drawable.female_9_1,
    )

    Row(
        modifier = Modifier.horizontalScroll(state = rememberScrollState(), enabled = true)
    ) {
        listColor.forEach {
            Image(
                painter = painterResource(id = it),
                contentDescription = "",
                Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .clickable { onClick(it) }
                )
        }
    }
}
