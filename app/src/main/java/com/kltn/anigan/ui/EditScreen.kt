package com.kltn.anigan.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.layouts.footers.EditFooter
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.BitmapUtils.Companion.getScreenWidth
import com.kltn.anigan.utils.UriUtils
import com.kltn.anigan.utils.UriUtils.Companion.saveBitmapAndGetUri

@Composable
fun EditScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: DocsViewModel
) {
    val context = LocalContext.current
    val bitmap = viewModel.bitmap
    if (bitmap == null) navController.popBackStack()
    val screenWidth = getScreenWidth(context)
    val croppedSize =
        BitmapUtils.cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble())
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
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Header
        Header(
            navController = navController,
            viewModel = viewModel
        )
        Canvas(
            modifier = Modifier
                .clipToBounds()
                .width(BitmapUtils.dpFromPx(context, croppedSize[0].toFloat()).dp)
                .height(BitmapUtils.dpFromPx(context, croppedSize[1].toFloat()).dp)
        ) {
            scaledBitmap?.let {
                drawImage(
                    image = it.asImageBitmap()
                )
            }
        }

        EditFooter(navController, viewModel, isLoading = false)
    }
}

@Composable
private fun Header(
    navController: NavController,
    viewModel: DocsViewModel
) {
    val context = LocalContext.current
    val bitmap = viewModel.bitmap
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it?.let {
                viewModel.uri.value = it.toString()
                val uri = UriUtils.encodeUri(viewModel.uri.value)
                viewModel.bitmap = BitmapUtils.getBitmapFromUri(uri, context)
            }
        }
    )
    Row(
        Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            Modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(20.dp)
                .clickable {
                    navController.navigate(Routes.MAIN_SCREEN.route)
                },
        )

        Row(
            Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_image_24),
                contentDescription = "icon_change_image",
                Modifier
                    .size(30.dp)
                    .clickable {
                        galleryLauncher.launch("image/*")
                    },
            )

            Spacer(modifier = Modifier.width(20.dp))

            //Icon notification
            OutlinedButton(
                onClick = {
                    if (bitmap != null) {
                        saveBitmapAndGetUri(context, bitmap)
                    }
                    Toast.makeText(context, "Successfully!", Toast.LENGTH_LONG).show()
                }
            ) {
                Text(text = "Save", color = Color.White)
            }
        }
    }
}