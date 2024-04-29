package com.kltn.anigan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.math.min

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

        // Function to create a square bitmap from a bitmap
        fun createSquareBitmap(bitmap: Bitmap): Bitmap {
            val size = min(bitmap.width, bitmap.height)
            val startX = (bitmap.width - size) / 2
            val startY = (bitmap.height - size) / 2
            return Bitmap.createBitmap(bitmap, startX, startY, size, size)


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
    }

}