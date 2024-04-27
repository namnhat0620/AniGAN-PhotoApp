package com.kltn.anigan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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