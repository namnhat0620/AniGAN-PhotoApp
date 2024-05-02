package com.kltn.anigan.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kltn.anigan.R
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.enums.EditType
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.MyCanvasView
import com.kltn.anigan.ui.shared.layouts.footers.BaseFooter
import com.kltn.anigan.ui.shared.layouts.footers.ListColor
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.BitmapUtils.Companion.createSingleImageFromMultipleImages
import com.kltn.anigan.utils.BitmapUtils.Companion.generateBitmap
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUri
import com.kltn.anigan.utils.BitmapUtils.Companion.getScreenWidth
import com.kltn.anigan.utils.UriUtils.Companion.encodeUri
import com.kltn.anigan.utils.UriUtils.Companion.saveBitmapAndGetUri
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddTextScreen(
    navController: NavController = rememberNavController(),
    uri: String? = "",
) {
    if (uri.isNullOrEmpty()) return
    val context = LocalContext.current
    val viewModel = remember { DocsViewModel() }
    val screenWidth = getScreenWidth(context)
    val bitmap = getBitmapFromUri(Uri.parse(encodeUri(uri)), context)

    val croppedSize =
        BitmapUtils.cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble())
    viewModel.x.floatValue = (croppedSize[0].toInt() / 2).toFloat() - viewModel.textSize.floatValue * 3
    viewModel.y.floatValue =  (croppedSize[1].toInt() / 2).toFloat()
    val scaledBitmap = bitmap?.let {
        Bitmap.createScaledBitmap(
            it,
            croppedSize[0].toInt(),
            croppedSize[1].toInt(),
            false
        )
    }

    val scrollPermit = remember { RequestDisallowInterceptTouchEvent() }
    scrollPermit.invoke(viewModel.canvasEdit.value)
    val scope = rememberCoroutineScope()
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .verticalScroll(state = rememberScrollState(), enabled = true),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            contentAlignment = Alignment.TopCenter
        ) {
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
            AndroidView(
                factory = {
                    MyCanvasView(context = it, viewModel = viewModel, editType = EditType.TEXT).apply {
                        this.bitmap = bitmap
                    }
                },
                update = {
                    it.bitmap = bitmap
                    viewModel.undoCanvas.value = false
                    if (viewModel.saveCanvas.value && bitmap != null && viewModel.text.value.isNotEmpty() && !isSaved) {
                        scope.launch {
                            viewModel.saveCanvas.value = false
                            val drawBitmap = generateBitmap(it, it.width, it.height)
                            val scaledBitmap2 = Bitmap.createScaledBitmap(
                                drawBitmap,
                                bitmap.width,
                                bitmap.height,
                                true
                            )
                            val combinedBitmap =
                                createSingleImageFromMultipleImages(bitmap, scaledBitmap2)

                            val newUri =
                                saveBitmapAndGetUri(context, combinedBitmap) ?: Uri.EMPTY
                            navController.navigate("${Routes.EDIT_SCREEN.route}?uri=$newUri")
                        }
                    }
                }
            )
        }

        Footer(
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
private fun Footer(
    viewModel: DocsViewModel,
    navController: NavController = rememberNavController(),
) {
    val undoCanvas = viewModel.undoCanvas.value
    Column(
        Modifier.background(Color.Black)
    ) {
        TextField(value = viewModel.text.value, onValueChange = {
            viewModel.text.value = it
        }, modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.background_gray)))
        Spacer(Modifier.height(12.dp))
        Text(text = "Text Size", color = Color.White)
        Slider(value = viewModel.textSize.floatValue, valueRange = 0f..200f, onValueChange = {
            viewModel.textSize.floatValue = it
        })
        ListColor {
            viewModel.color.value = it
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.undoCanvas.value = true
            },
            enabled = !undoCanvas
        ) {
            Text(
                text = "Undo",
                color = Color.White.copy(alpha = if (!undoCanvas) 1f else 0.4f),
                fontSize = 20.sp,
                modifier = Modifier.clickable {
                    viewModel.undoCanvas.value = true
                }
            )
        }
        BaseFooter(navController = navController, viewModel = viewModel)
    }
}
