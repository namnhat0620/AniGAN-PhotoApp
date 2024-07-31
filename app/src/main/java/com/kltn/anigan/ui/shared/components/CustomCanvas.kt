package com.kltn.anigan.ui.shared.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.enums.EditType
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.BitmapUtils.Companion.cropWidthHeight
import com.kltn.anigan.utils.BitmapUtils.Companion.dpFromPx
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromDrawable
import com.kltn.anigan.utils.BitmapUtils.Companion.getScreenWidth

@Suppress("SameParameterValue")
@SuppressLint("ViewConstructor")
@ExperimentalComposeUiApi
class MyCanvasView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val viewModel: DocsViewModel,
    private val editType: EditType
) : AbstractComposeView(context, attrs, defStyleAttr) {
    var bitmap: Bitmap? = null
    val path = Path()

    @Composable
    override fun Content() {
        when (editType) {
            EditType.BRUSH -> DrawHairCanvas()
            EditType.TEXT -> DrawTextCanvas()
            EditType.CROP -> CropImageCanvas()
        }
    }
//
//    @Composable
//    fun DrawPathCanvas() {
//        val context = LocalContext.current
//        val screenWidth = getScreenWidth(context)
//        val croppedSize =
//            cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble(), false)
//        val scrollPermit = remember { RequestDisallowInterceptTouchEvent() }
//        val undo = viewModel.undoCanvas.value
//        if (undo) {
//            path.reset()
//            viewModel.undoCanvas.value = false
//        }
//        var recompose = viewModel.recompose.intValue
//        val canvasEdit = viewModel.canvasEdit.value
//        scrollPermit.invoke(canvasEdit)
//        Canvas(modifier = Modifier
//            .clipToBounds()
//            .width(dpFromPx(context, croppedSize[0].toFloat()).dp)
//            .height(dpFromPx(context, croppedSize[1].toFloat()).dp)
//            .pointerInteropFilter(
//                requestDisallowInterceptTouchEvent = scrollPermit
//            ) { event ->
//                val x = event.x
//                val y = event.y
//                if (canvasEdit)
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            scrollPermit.invoke(true)
//                            recompose++
//                            viewModel.recompose.intValue = recompose
//                            path.moveTo(x, y)
//                        }
//
//                        MotionEvent.ACTION_MOVE -> {
//                            scrollPermit.invoke(true)
//                            recompose++
//                            viewModel.recompose.intValue = recompose
//                            path.lineTo(x, y)
//                        }
//
//                        MotionEvent.ACTION_UP -> {
//                            scrollPermit.invoke(false)
//                            recompose = 0
//                            viewModel.recompose.intValue = recompose
//                        }
//                    }
//                true
//            }
//        ) {
//            val color = viewModel.color.value
//            val opacity = viewModel.opacity.floatValue / 100f
//            val alphaColor = color.copy(alpha = color.alpha * opacity)
//            drawPath(
//                path = path,
//                color = alphaColor,
//                style = Stroke(
//                    width = viewModel.brushSize.floatValue,
//                    cap = StrokeCap.Round,
//                    join = StrokeJoin.Round
//                ),
//            )
//        }
//    }

    @Composable
    fun DrawTextCanvas() {
        val context = LocalContext.current
        val screenWidth = getScreenWidth(context)
        val croppedSize =
            cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble(), false)
        val scrollPermit = remember { RequestDisallowInterceptTouchEvent() }
        val undo = viewModel.undoCanvas.value
        if (undo) {
            viewModel.text.value = ""
            viewModel.undoCanvas.value = false
        }
        var recompose = viewModel.recompose.intValue
        val canvasEdit = viewModel.canvasEdit.value
        scrollPermit.invoke(canvasEdit)
        Canvas(modifier = Modifier
            .clipToBounds()
            .width(dpFromPx(context, croppedSize[0].toFloat()).dp)
            .height(dpFromPx(context, croppedSize[1].toFloat()).dp)
            .pointerInteropFilter(
                requestDisallowInterceptTouchEvent = scrollPermit
            ) { event ->
                val x = event.x
                val y = event.y
                if (canvasEdit)
                    when (event.action) {
                        MotionEvent.ACTION_MOVE -> {
                            scrollPermit.invoke(true)
                            recompose++
                            viewModel.recompose.intValue = recompose
                            viewModel.x.floatValue = x - viewModel.textSize.floatValue * 3
                            viewModel.y.floatValue = y
                        }

                        MotionEvent.ACTION_UP -> {
                            scrollPermit.invoke(false)
                            recompose = 0
                            viewModel.recompose.intValue = recompose
                        }
                    }
                true
            }
        ) {
            val x = viewModel.x.floatValue
            val y = viewModel.y.floatValue

            drawIntoCanvas { canvas ->
                // Draw your text here
                val text = viewModel.text.value
                val paint = Paint().asFrameworkPaint()
                paint.textSize = viewModel.textSize.floatValue
                paint.color = viewModel.color.value.toArgb()
                paint.typeface = Typeface.createFromAsset(context.assets, "CCBattleCry-Regular.ttf")
                canvas.nativeCanvas.drawText(text, x, y, paint)
            }
        }
    }

    @Composable
    fun DrawHairCanvas() {
        val context = LocalContext.current
        val screenWidth = getScreenWidth(context)
        val croppedSize =
            cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble(), false)
        val scrollPermit = remember { RequestDisallowInterceptTouchEvent() }
        val undo = viewModel.undoCanvas.value
        if (undo) {
            viewModel.hairResourceId.intValue = 0
            viewModel.undoCanvas.value = false
        }
        var recompose = viewModel.recompose.intValue
        val canvasEdit = viewModel.canvasEdit.value

        val hairBitmap = getBitmapFromDrawable(context, viewModel.hairResourceId.intValue)
        val croppedHairSize =
            cropWidthHeight(
                hairBitmap.width,
                hairBitmap.height,
                viewModel.hairSizeAlpha.floatValue.toDouble()
            )
        val scaledHairBitmap = hairBitmap.let {
            Bitmap.createScaledBitmap(
                it,
                croppedHairSize[0].toInt(),
                croppedHairSize[1].toInt(),
                false
            )
        }

        scrollPermit.invoke(canvasEdit)
        Canvas(modifier = Modifier
            .clipToBounds()
            .width(dpFromPx(context, croppedSize[0].toFloat()).dp)
            .height(dpFromPx(context, croppedSize[1].toFloat()).dp)
            .pointerInteropFilter(
                requestDisallowInterceptTouchEvent = scrollPermit
            ) { event ->
                val x = event.x
                val y = event.y
                if (canvasEdit)
                    when (event.action) {
                        MotionEvent.ACTION_MOVE -> {
                            scrollPermit.invoke(true)
                            recompose++
                            viewModel.recompose.intValue = recompose
                            viewModel.x.floatValue = x
                            viewModel.y.floatValue = y
                        }

                        MotionEvent.ACTION_UP -> {
                            scrollPermit.invoke(false)
                            recompose = 0
                            viewModel.recompose.intValue = recompose
                        }
                    }
                true
            }
        ) {
            val x = viewModel.x.floatValue - scaledHairBitmap.width / 2
            val y = viewModel.y.floatValue - scaledHairBitmap.height / 2

            val alpha = viewModel.opacity.floatValue / 100
            // Draw the image on the canvas with the calculated position and size
            drawImage(
                image = BitmapUtils.rotateBitmap(scaledHairBitmap, viewModel.angle.floatValue).asImageBitmap(),
                topLeft = Offset(x, y),
                alpha = alpha
            )

        }
    }

    @Composable
    fun CropImageCanvas() {
//        val context = LocalContext.current
//        val screenWidth = getScreenWidth(context)
//        val croppedSize =
//            cropWidthHeight(bitmap?.width, bitmap?.height, screenWidth.toDouble(), false)
//        val scrollPermit = remember { RequestDisallowInterceptTouchEvent() }
//        val cropRect = viewModel.cropRect.value
//        val canvasEdit = viewModel.canvasEdit.value
//        var recompose = viewModel.recompose.intValue
//        val rectX = viewModel.x.floatValue
//        val rectY = viewModel.y.floatValue
//        Canvas(
//            modifier = Modifier
//                .clipToBounds()
//                .width(dpFromPx(context, croppedSize[0].toFloat()).dp)
//                .height(dpFromPx(context, croppedSize[1].toFloat()).dp)
//            .pointerInteropFilter(
//                requestDisallowInterceptTouchEvent = scrollPermit
//            ) { event ->
//                val x = event.x
//                val y = event.y
//                if (canvasEdit)
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            scrollPermit.invoke(true)
//                            recompose++
//                            if(rectX==x || ) {
//                                viewModel.move.value = false
//                                viewModel.resize.value = true
//                            }
//                            else {
//                                viewModel.move.value = true
//                                viewModel.resize.value = false
//                            }
//                        }
//
//                        MotionEvent.ACTION_MOVE -> {
//                            scrollPermit.invoke(true)
//                            recompose++
//                            viewModel.recompose.intValue = recompose
//                            path.lineTo(x, y)
//                        }
//
//                        MotionEvent.ACTION_UP -> {
//                            scrollPermit.invoke(false)
//                            recompose = 0
//                            viewModel.recompose.intValue = recompose
//                        }
//                    }
//                true
//            }
////                .graphicsLayer(
////                    scaleX = cropScaleX.toFloat(),
////                    scaleY = cropScaleY.toFloat(),
////                    translationX = -(cropRect.left).toFloat(),
////                    translationY = -(cropRect.top).toFloat()
////                )
//        )  {
//            // Vẽ hình chữ nhật đầy bên trong với màu trắng
//            drawRect(
//                color = Color.Black.copy(alpha = 0.4f),
//                size = Size(croppedSize[0].toFloat(), croppedSize[1].toFloat()),
//                style = Stroke(200.dp.toPx())
//
//            )
//
//        }
    }
}