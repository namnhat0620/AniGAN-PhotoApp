package com.kltn.anigan.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class UriUtils {

    companion object {

        fun encodeUri(uriString: String): Uri {
            val uri = Uri.parse(uriString)
            val encodedUriString = Uri.encode(uri.lastPathSegment)
            return Uri.parse(uriString.replace(uri.lastPathSegment ?: "", encodedUriString))
        }

//        fun saveImageFromUrl(imageUrl: String): Uri? {
//            val url = URL(imageUrl)
//            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//            connection.doInput = true
//            connection.connect()
//            val input = connection.inputStream
//
//            val saveDirectory = "image_${System.currentTimeMillis()}.jpg"
//            val directory = File(saveDirectory)
//            if (!directory.exists()) {
//                directory.mkdirs()
//            }
//
//            try {
//                // Extract the filename from the URL
//                val filename = imageUrl.substringAfterLast("/")
//
//                // Build the path to save the image
//                val savePath = File(saveDirectory, filename)
//
//                // Download the image from the URL
//                FileOutputStream(savePath).use { outputStream ->
//                    val data = ByteArray(1024)
//                    var bytesRead = input.read(data)
//                    while (bytesRead != -1) {
//                        outputStream.write(data, 0, bytesRead)
//                        bytesRead = input.read(data)
//                    }
//                }
//
//
//                println("Image saved successfully.")
//                return Uri.fromFile(savePath)
//            } catch (e: Exception) {
//                println("Failed to save the image: ${e.message}")
//                return null
//            }
//        }

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
                val uri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

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

        fun ContentResolver.getFileName(capturedImageUri: Uri): String {
            var name = ""
            val returnCursor = this.query(capturedImageUri, null, null, null, null)
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                name = returnCursor.getString(nameIndex)
                returnCursor.close()
            }

            return name
        }

        fun saveImageFromUrl(context: Context, imageUrl: String): Uri {
            val url = URL(imageUrl)

            // Create a directory for saving the image
            val directory = File(context.externalCacheDir, "")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Extract the filename from the URL
            val filename = imageUrl.substringAfterLast("/")

            // Build the path to save the image
            val savePath = File(directory, filename)

            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream

            val fileOutputStream = FileOutputStream(savePath)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
            }

            fileOutputStream.flush()
            fileOutputStream.close()
            input.close()

            val imagePath = savePath.absolutePath
            val imageFile = File(imagePath)
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
        }

        fun shareImage(shareImageLauncher: ActivityResultLauncher<Intent>, imageUri: Uri) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Image")
            shareImageLauncher.launch(chooser)
        }
    }
}
