package com.kltn.anigan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kltn.anigan.R

@Composable
fun AIToolScreen() {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
        ) {
            Header()
            InsertImage()
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Row (
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(17.dp),
        )
        Row {
            Image(
                painter = painterResource(id = R.drawable.icon_change_image),
                contentDescription = "icon_change_image",
                modifier
                    .padding(start = 12.dp, top = 16.dp)
                    .size(17.dp),
            )

            //Icon notification
            Image(
                painter = painterResource(id = R.drawable.icon_library),
                contentDescription = "icon_library",
                modifier
                    .padding(start = 17.dp, top = 16.dp, end = 12.dp)
                    .size(17.dp)
            )
        }


    }
}

@Composable
private fun InsertImage(modifier: Modifier = Modifier) {
    Row (
        modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.insert_image_AI_Tools))
            .background(color = colorResource(id = R.color.background)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ){
        Image(
            painter = painterResource(id = R.drawable.insert_img) ,
            contentDescription = "Insert Image",
            modifier = modifier

        )
    }

}
