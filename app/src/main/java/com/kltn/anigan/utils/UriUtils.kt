package com.kltn.anigan.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL

class UriUtils {

    companion object {

        fun encodeUri(uriString: String): String {
            val uri = Uri.parse(uriString)
            val encodedUriString = Uri.encode(uri.lastPathSegment)
            return uriString.replace(uri.lastPathSegment ?: "", encodedUriString)
        }

        fun saveImageFromUrl(url: String): Uri? {
            val saveDirectory = "image_${System.currentTimeMillis()}.jpg"
            val directory = File(saveDirectory)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            try {
                // Extract the filename from the URL
                val filename = url.substringAfterLast("/")

                // Build the path to save the image
                val savePath = File(saveDirectory, filename)

                // Download the image from the URL
                BufferedInputStream(URL(url).openStream()).use { inputStream ->
                    FileOutputStream(savePath).use { outputStream ->
                        val data = ByteArray(1024)
                        var bytesRead = inputStream.read(data)
                        while (bytesRead != -1) {
                            outputStream.write(data, 0, bytesRead)
                            bytesRead = inputStream.read(data)
                        }
                    }
                }

                println("Image saved successfully.")
                return Uri.fromFile(savePath)
            } catch (e: Exception) {
                println("Failed to save the image: ${e.message}")
                return null
            }
        }

        fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
            // Create a file in the cache directory
            val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.jpg")
            return try {
                // Write the bitmap to the file
                val outputStream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // Insert the image into MediaStore to get a content URI
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                    put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                }
                val contentResolver = context.contentResolver
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Move the bitmap file to the MediaStore content URI
                uri?.let {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        file.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                }

                // Delete the original file
                file.delete()

                // Return the content URI
                uri
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun saveUriToLibrary(context: Context, uri: Uri, displayName: String): Uri? {
            val contentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }

            // Insert the URI into MediaStore
            val uriInserted = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uriInserted?.let { outputStream ->
                contentResolver.openOutputStream(outputStream)?.use { outputStream2 ->
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.copyTo(outputStream2)
                    }
                }
            }
            return uriInserted
        }
    }
}
