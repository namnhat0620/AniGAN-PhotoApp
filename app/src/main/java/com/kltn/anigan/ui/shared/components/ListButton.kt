package com.kltn.anigan.ui.shared.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.routes.Routes

@Composable
public fun FuncButton(imageId: Int, text: String,  onClick: () -> Unit = {}, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "edit_btn",
            modifier.size(30.dp)
        )
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
public fun ListButton(modifier: Modifier = Modifier, navController: NavController?= null) {
    Row (
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    )
    {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            FuncButton2(R.drawable._crop, "Crop",
                onClick = {
                    navController?.navigate(Routes.AI_TOOLS.route)
                },
                modifier = modifier)
            FuncButton2(R.drawable._text, "Text",
                modifier = modifier)
            FuncButton2(R.drawable._emoji, "Emoji",
                modifier = modifier)
            FuncButton2(R.drawable._filter, "Filters",
                onClick = {
                    navController?.navigate(Routes.FILLTER_TOOL.route)
                },
                modifier = modifier)
            FuncButton2(R.drawable._rotate, "Rotate\nFlip",
                modifier = modifier)
        }

    }
}

@Composable
private fun FuncButton2(imageId: Int, text: String,  onClick: () -> Unit = {}, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "edit_btn",
            modifier.size(23.dp)
        )
        Spacer(modifier = modifier.height(5.dp))
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 15.sp
        )
    }
}