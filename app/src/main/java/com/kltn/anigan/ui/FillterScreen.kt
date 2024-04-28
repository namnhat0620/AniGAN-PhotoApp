package com.kltn.anigan.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.ui.shared.components.FuncButton
import com.kltn.anigan.ui.shared.layouts.Footer
import com.kltn.anigan.utils.UriUtils.Companion.encodeUri

@Composable
fun FillterScreen(navController: NavController,uri: Uri) {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxWidth(),

    ){
        Column {
            var colorFilter by remember { mutableStateOf<ColorFilter?>(null) }
//
//            if(capturedImageUri.path?.isNotEmpty() == true) {
//                Image(
//                    painter = rememberImagePainter(capturedImageUri),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(1f),
//                    contentScale = ContentScale.Crop,
//                    colorFilter = colorFilter
//                )
//            }
//            else {
//                Image(
//                    painter = painterResource(id = R.drawable.insert_img) ,
//                    contentDescription = "Insert Image"
//                )
//            }

            Row (
                modifier = Modifier
                    .height(290.dp)
                    .fillMaxWidth()
                ,
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            )
            {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {


                    FuncButton(R.drawable.graphics_bw_1, "Black \n& White",
                        modifier = Modifier
                            .clickable {
                                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                            }
                    )
                    FuncButton(R.drawable.graphics_blendmode_1, "Blend",
                        modifier=  Modifier
                            .clickable {
                                colorFilter = ColorFilter.tint(
                                    color = Color.Green,
                                    blendMode = BlendMode.Darken
                                )
                            }
                    )
                    FuncButton(R.drawable.graphics_inverted_1, "Inverted",
                        modifier = Modifier
                            .clickable {
                                val colorMatrix = floatArrayOf(
                                    -1f, 0f, 0f, 0f, 255f,
                                    0f, -1f, 0f, 0f, 255f,
                                    0f, 0f, -1f, 0f, 255f,
                                    0f, 0f, 0f, 1f, 0f
                                )

                                colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
                            }
                    )
                    FuncButton(R.drawable.graphics_colormatrix_1, "Contrast",
                        modifier = Modifier
                            .clickable {
                                val contrast = 2f // 0f..10f (1 should be default)
                                val brightness = -180f // -255f..255f (0 should be default)
                                val colorMatrix = floatArrayOf(
                                    contrast, 0f, 0f, 0f, brightness,
                                    0f, contrast, 0f, 0f, brightness,
                                    0f, 0f, contrast, 0f, brightness,
                                    0f, 0f, 0f, 1f, 0f
                                )
                                colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
                            })

                }

            }
            Footer(navController)
        }
    }

}


//fun saveImageToInternalStorage(context: Context, uri: Uri) {
//    val inputStream = context.contentResolver.openInputStream(uri)
//    val outputStream = context.openFileOutput("image.jpg", Context.MODE_PRIVATE)
//    inputStream?.use { input ->
//        outputStream.use { output ->
//            input.copyTo(output)
//        }
//    }
//}

//fun rememberColorFilter(color: Color, blendMode: BlendMode): ColorFilter {
//    return remember {
//        ColorFilter.tint(
//            color = color,
//            blendMode = blendMode
//        )
//    }
//}



