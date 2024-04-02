package com.kltn.anigan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.kltn.anigan.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditScreen(
    navController: NavController,
    uri: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.background(Color.Black)
    ) {
        Header(navController = navController)
        GlideImage(
            model = uri,
            contentDescription = null,
            failure = placeholder(R.drawable.default_image),
            modifier = modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.insert_image_AI_Tools))
        )
        Row(
            modifier              .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomButton(
                drawableId = R.drawable.icon_share,
                text = "Share",
                backgroundColorId = R.color.background_gray
            )
            Spacer(modifier = modifier.width(10.dp))
            CustomButton(
                drawableId = R.drawable.icon_download,
                text = "Download",
                backgroundColorId = R.color.background_blue
            )
        }
    }
}

@Composable
fun Header(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(17.dp)
                .clickable {
                    navController.popBackStack()
                },
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

@Composable
fun CustomButton(
    drawableId: Int,
    text: String,
    backgroundColorId: Int,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { /*TODO*/ },
        modifier
            .width(160.dp)
            .height(46.dp)
        ,
        colors = ButtonDefaults.buttonColors(colorResource(id = backgroundColorId))
    ) {
        Row(
            modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                modifier.size(16.dp)
            )
            Spacer(modifier = modifier.width(6.dp))
            Text(text = text, fontSize = 18.sp)
        }
    }
}