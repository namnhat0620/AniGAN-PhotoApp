package com.kltn.anigan.ui.shared.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.routes.Routes

@Composable
fun rememberColorFilter(color: Color, blendMode: BlendMode): ColorFilter {
    return remember {
        ColorFilter.tint(
            color = color,
            blendMode = blendMode
        )
    }
}


@Composable
public fun ListButton_Fillter(modifier: Modifier = Modifier, navController: NavController?= null,colorFilter: ColorFilter) {
    Row (
        modifier
            .height(290.dp)
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    )
    {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FuncButton(R.drawable.graphics_bw_1, "Black \n& White",
                modifier = Modifier
                    .clickable {

                    }
                )
            FuncButton(R.drawable.graphics_blendmode_1, "Blend",
                modifier=  modifier

            )
            FuncButton(R.drawable.graphics_inverted_1, "Inverted",
                modifier = modifier)
            FuncButton(R.drawable.graphics_colormatrix_1, "Contrast",
                modifier = modifier)

        }

    }
}




