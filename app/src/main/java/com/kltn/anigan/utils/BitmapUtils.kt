package com.kltn.anigan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class BitmapUtils {
    companion object {
        fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
            return try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun rotate90(bitmap: Bitmap): Bitmap {
            //Rotate 90
            val matrix = Matrix()
            matrix.postRotate(90f)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        suspend fun getBitmapFromUrl(urlString: String, context: Context): Bitmap? {
            return withContext(Dispatchers.IO) {
                try {
                    Glide.with(context)
                        .asBitmap()
                        .load(urlString)
                        .submit()
                        .get()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }


        fun getScreenWidth(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        fun cropWidthHeight(bmWidth: Int?, bmHeight: Int?, target: Double, setToTargetWidth: Boolean = false): List<Double> {
            var dWidth: Double = bmWidth?.toDouble() ?: return listOf(0.0, 0.0)
            var dHeight: Double = bmHeight?.toDouble() ?: return listOf(0.0, 0.0)
            val width: Double = dWidth
            val height: Double = dHeight
            val dominant = if(dWidth >= dHeight) {dWidth} else {dHeight}
            if(dominant > target) {
                //Zoom out
                val dValue = dominant - target
                if(dWidth >= dHeight) {
                    dWidth = width - dValue
                    dHeight = height - (dValue * (height/width))
                }
                else {
                    dWidth = width - (dValue * (width/height))
                    dHeight = height - dValue
                }
            }
            else if(dominant < target) {
                //Zoom in
                val dValue = target - dominant
                if(dWidth >= dHeight) {
                    dWidth = width + dValue
                    dHeight = height + (dValue * (height/width))
                } else {
                    dWidth = width + (dValue * (width/height))
                    dHeight = height + dValue
                }
            }

            if(setToTargetWidth) {
                while (dWidth < target) {
                    dWidth++
                    dHeight++
                }
            }
            return listOf(dWidth, dHeight)
        }

        fun dpFromPx(context: Context, px: Float): Float {
            return px / context.resources.displayMetrics.density
        }

        fun generateBitmap(view: View, targetWidth: Int, targetHeight: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.layout(view.left, view.top, view.right, view.bottom)
            view.draw(canvas)
            return bitmap
        }

        fun createSingleImageFromMultipleImages(firstImage: Bitmap, secondImage: Bitmap): Bitmap {
            val result = Bitmap.createBitmap(firstImage.width, firstImage.height, firstImage.config)
            val canvas = Canvas(result)
            canvas.drawBitmap(firstImage, 0f, 0f, null)
            canvas.drawBitmap(secondImage, 0f, 0f, null)
            return result
        }

        fun applyColorFilter(bitmap: Bitmap, colorMatrix: ColorMatrix): Bitmap {
            val filteredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(filteredBitmap)
            val paint = Paint()

            // Apply the color filter
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

            // Draw the bitmap onto the canvas with the color filter applied
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            return filteredBitmap
        }
    }
}