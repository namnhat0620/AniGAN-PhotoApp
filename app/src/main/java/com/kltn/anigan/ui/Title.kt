package com.kltn.anigan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kltn.anigan.R


@Composable
internal fun Title(text1: String, text2: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text1,
            modifier= Modifier.alignByBaseline(),
            color = colorResource(id = R.color.white),
            fontSize = 25.sp
        )
        Text(
            text = text2,
            modifier = Modifier.alignByBaseline(),
            color = colorResource(id = R.color.white),
            fontSize = 15.sp,
        )
    }

}