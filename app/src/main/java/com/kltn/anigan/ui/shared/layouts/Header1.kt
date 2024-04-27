package com.kltn.anigan.ui.shared.layouts

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.ui.createImageFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Objects
import kotlin.math.min


@Composable
fun Header1(navController: NavController,
            capturedImageUri: Uri,
            modifier: Modifier = Modifier
            )
{
    Row (
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(17.dp)
                .clickable {
                           navController.popBackStack()
                },
        )
        Row {
            val context = LocalContext.current
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider",
                file
            )
            Button(
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Black, contentColor = Color.White),
                onClick = {
                    val bitmap = getBitmapFromUri(capturedImageUri, context)
                    if (bitmap  != null){
                        val squareBitmap = createSquareBitmap(bitmap)
                        val newUri = saveBitmapAndGetUri(context, squareBitmap)
                    }


                    // Save squareBitmap to file and get the n
                },
            ) {
                Text("Save")
            }
        }
    }
}

private fun createSquareBitmap(bitmap: Bitmap): Bitmap {
    val size = min(bitmap.width, bitmap.height)
    val startX = (bitmap.width - size) / 2
    val startY = (bitmap.height - size) / 2

    //Rotate 90
    val matrix = Matrix()
    matrix.postRotate(90f)
    return Bitmap.createBitmap(bitmap, startX, startY, size, size, matrix, true)
}

private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
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
