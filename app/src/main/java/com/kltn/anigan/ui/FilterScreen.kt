package com.kltn.anigan.ui

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.FuncButton
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.BitmapUtils.Companion.applyColorFilter
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUri
import com.kltn.anigan.utils.UriUtils.Companion.encodeUri
import com.kltn.anigan.utils.UriUtils.Companion.saveBitmapAndGetUri

@Composable
fun FilterScreen(navController: NavController, uri: String?) {
    if (uri.isNullOrEmpty()) navController.popBackStack()
    val context = LocalContext.current
    val viewModel = remember { DocsViewModel() }
    val screenWidth = BitmapUtils.getScreenWidth(context)
    val bitmap = uri?.let {
        getBitmapFromUri(Uri.parse(encodeUri(uri)), context)
    }
    if (bitmap == null) navController.popBackStack()
    val croppedSize =
        BitmapUtils.cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble())
    viewModel.x.floatValue =
        (croppedSize[0].toInt() / 2).toFloat() - viewModel.textSize.floatValue * 3
    viewModel.y.floatValue = (croppedSize[1].toInt() / 2).toFloat()
    val scaledBitmap = bitmap?.let {
        Bitmap.createScaledBitmap(
            it,
            croppedSize[0].toInt(),
            croppedSize[1].toInt(),
            false
        )
    }

    var colorMatrix by remember {
        mutableStateOf(ColorMatrix())
    }
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
        ) {
        Canvas(
            modifier = Modifier
                .clipToBounds()
                .width(BitmapUtils.dpFromPx(context, croppedSize[0].toFloat()).dp)
                .height(BitmapUtils.dpFromPx(context, croppedSize[1].toFloat()).dp)
        ) {
            scaledBitmap?.let {
                val bitmapApplyFilter = applyColorFilter(it, colorMatrix = colorMatrix)
                drawImage(
                    image = bitmapApplyFilter.asImageBitmap()
                )
            }
        }

        bitmap?.let {
            Footer(navController = navController, bitmap, colorMatrix) {
                colorMatrix = it
            }
        }
    }


}

@Composable
private fun Footer(
    navController: NavController,
    bitmap: Bitmap,
    colorMatrix: ColorMatrix,
    onClick: (ColorMatrix) -> Unit
) {

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FuncButton(R.drawable.graphics_bw_1, "Black \n& White",
                modifier = Modifier
                    .clickable {
                        onClick(
                            ColorMatrix(
                                (floatArrayOf(
                                    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                                    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                                    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                            )
                        )
                    }
            )
            FuncButton(R.drawable.graphics_blendmode_1, "Blend",
                modifier = Modifier
                    .clickable {
                        onClick(
                            ColorMatrix(
                                floatArrayOf(
                                    0.393f, 0.7689999f, 0.189f, 0f, 0f,
                                    0.349f, 0.6859999f, 0.16799998f, 0f, 0f,
                                    0.272f, 0.5339999f, 0.131f, 0f, 0f,
                                    0f,     0f,         0f,          1f, 0f
                                )
                            )
                        )
                    }
            )
            FuncButton(R.drawable.graphics_inverted_1, "Inverted",
                modifier = Modifier
                    .clickable {
                        onClick(
                            ColorMatrix(
                                floatArrayOf(
                                    -1f, 0f, 0f, 0f, 255f,
                                    0f, -1f, 0f, 0f, 255f,
                                    0f, 0f, -1f, 0f, 255f,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            )
                        )
                    }
            )
            FuncButton(R.drawable.graphics_colormatrix_1, "Contrast",
                modifier = Modifier
                    .clickable {
                        val contrast = 2f // 0f..10f (1 should be default)
                        val brightness = -180f // -255f..255f (0 should be default)
                        onClick(
                            ColorMatrix(
                                floatArrayOf(
                                    contrast, 0f, 0f, 0f, brightness,
                                    0f, contrast, 0f, 0f, brightness,
                                    0f, 0f, contrast, 0f, brightness,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            )
                        )
                    }
            )
        }
        BaseFooter(navController = navController, bitmap, colorMatrix)
    }

}

@Composable
private fun BaseFooter(
    navController: NavController,
    bitmap: Bitmap,
    colorMatrix: ColorMatrix
) {
    val context = LocalContext.current
    Row(
        Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            Modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(17.dp)
                .clickable {
                    navController.popBackStack()
                },
        )
        Row {
            Image(
                painter = painterResource(id = R.drawable._icon__check_),
                contentDescription = "icon_change_image",
                Modifier
                    .padding(start = 12.dp, top = 16.dp, end = 12.dp)
                    .size(17.dp)
                    .clickable {
                        val bitmapAppliedFilter = applyColorFilter(bitmap, colorMatrix)
                        val uriAppliedFilter = saveBitmapAndGetUri(context, bitmapAppliedFilter)
                        navController.navigate("${Routes.EDIT_SCREEN.route}?uri=$uriAppliedFilter")
                    }
            )
        }
    }
}

