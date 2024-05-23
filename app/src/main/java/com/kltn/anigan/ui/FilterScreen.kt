package com.kltn.anigan.ui

import android.graphics.Bitmap
import android.graphics.ColorMatrix
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.BitmapUtils.Companion.applyColorFilter

@Composable
fun FilterScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current
    val screenWidth = BitmapUtils.getScreenWidth(context)
    val bitmap = viewModel.bitmap
    if (bitmap == null) navController.popBackStack()
    val croppedSize =
        BitmapUtils.cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble())
    viewModel.x.floatValue =
        (croppedSize[0].toInt() / 2).toFloat() - viewModel.textSize.floatValue * 3
    viewModel.y.floatValue = (croppedSize[1].toInt() / 2).toFloat()
    viewModel.bitmap = bitmap
    val scaledBitmap = bitmap?.let {
        Bitmap.createScaledBitmap(
            it,
            croppedSize[0].toInt(),
            croppedSize[1].toInt(),
            false
        )
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
                val bitmapApplyFilter =
                    applyColorFilter(it, colorMatrix = viewModel.colorMatrix.value)
                drawImage(
                    image = bitmapApplyFilter.asImageBitmap()
                )
            }
        }

        Column {
            Footer(viewModel)
            BaseFooter(navController = navController, viewModel)
        }


    }
}


@Composable
private fun Footer(viewModel: DocsViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        FuncButton(
            viewModel, "Black \n& White", ColorMatrix(
                (floatArrayOf(
                    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            )
        )

        FuncButton(
            viewModel, "Blend", ColorMatrix(
                floatArrayOf(
                    0.393f, 0.7689999f, 0.189f, 0f, 0f,
                    0.349f, 0.6859999f, 0.16799998f, 0f, 0f,
                    0.272f, 0.5339999f, 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FuncButton(
            viewModel, "Inverted", ColorMatrix(
                floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )

        FuncButton(
            viewModel, "Contrast", ColorMatrix(
                floatArrayOf(
                    2f, 0f, 0f, 0f, -180f,
                    0f, 2f, 0f, 0f, -180f,
                    0f, 0f, 2f, 0f, -180f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        )
    }
}

@Composable
private fun FuncButton(
    viewModel: DocsViewModel,
    text: String,
    colorMatrix: ColorMatrix
) {
    val context = LocalContext.current
    val bitmap = viewModel.bitmap
    val croppedSize =
        BitmapUtils.cropWidthHeight(bitmap?.width, bitmap?.height, 100.0)
    val scaledBitmap = bitmap?.let {
        Bitmap.createScaledBitmap(
            it,
            croppedSize[0].toInt(),
            croppedSize[1].toInt(),
            false
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { viewModel.colorMatrix.value = colorMatrix }
    ) {
        Canvas(
            modifier = Modifier
                .clipToBounds()
                .width(BitmapUtils.dpFromPx(context, croppedSize[0].toFloat()).dp)
                .height(BitmapUtils.dpFromPx(context, croppedSize[1].toFloat()).dp)
        ) {
            val bitmapApplyFilter =
                scaledBitmap?.let { applyColorFilter(it, colorMatrix = colorMatrix) }
            if (bitmapApplyFilter != null) {
                drawImage(
                    image = bitmapApplyFilter.asImageBitmap()
                )
            }
        }
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun BaseFooter(
    navController: NavController,
    viewModel: DocsViewModel
) {
    val bitmap = viewModel.bitmap ?: return
    val colorMatrix = viewModel.colorMatrix.value

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
                .size(20.dp)
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
                    .size(20.dp)
                    .clickable {
                            val bitmapAppliedFilter = applyColorFilter(bitmap, colorMatrix)
                            viewModel.bitmap = bitmapAppliedFilter
                            navController.navigate(Routes.EDIT_SCREEN.route)
                    }
            )
        }
    }
}


