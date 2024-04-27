package com.kltn.anigan.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.api.LoadImageApi
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.domain.ImageType
import com.kltn.anigan.domain.LoadImageResponse
import com.kltn.anigan.ui.shared.components.GenerateSetting
import com.kltn.anigan.ui.shared.layouts.Header
import com.kltn.anigan.utils.BitmapUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.math.min

@Composable
fun AIToolScreen(navController: NavController) {
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

//    var referenceImageUrl by remember {
//        mutableStateOf<String?>(null)
//    }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column{
            Header(
                setCapturedImageUri = { newUri ->
                    if (newUri != null) {
                        capturedImageUri = newUri
                    }
                },
                navController
            )
            InsertImage(
                capturedImageUri,
                setCapturedImageUri = { newUri ->
                    capturedImageUri = newUri
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
//            var list by remember { mutableStateOf<List<ImageClassFromInternet>>(emptyList()) }

//            getRefImage { updatedList ->
//                // Update the contents of the list variable with the data returned from getRefImage
//                list = updatedList
//            }

//            Title(text1 = "Style", text2 = "More >")
//            PhotoLibrary(itemList = list, setReferenceImageUrl = { url ->
//                referenceImageUrl = url
//            })
        }

        GenerateSetting(
            capturedImageUri,
            "",
            navController
        )

    }
}

@Composable
private fun InsertImage(
    capturedImageUri: Uri,
    setCapturedImageUri: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            val bitmap = BitmapUtils.getBitmapFromUri(uri, context) ?: return@rememberLauncherForActivityResult
            val squareBitmap = createSquareBitmap(bitmap)

            // Save squareBitmap to file and get the new URI
            val newUri = saveBitmapAndGetUri(context, squareBitmap)
            if (newUri != null) {
                // Update capturedImageUri with the new URI
                setCapturedImageUri(newUri)
            }
        }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Row (
        modifier
            .background(color = colorResource(id = R.color.background_gray))
            .clickable {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ){
        if(capturedImageUri.path?.isNotEmpty() == true) {
            Image(
                painter = rememberImagePainter(capturedImageUri),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.insert_img) ,
                contentDescription = "Insert Image"
            )
        }
    }
}

@Composable
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}

private fun getRefImage(onImageListLoaded: (List<ImageClassFromInternet>) -> Unit) {
    LoadImageApi().getRefImage(ImageType.REFERENCE_IMAGE.type).enqueue(object: Callback<LoadImageResponse>{
        override fun onResponse(
            call: Call<LoadImageResponse>,
            response: Response<LoadImageResponse>
        ) {
            if(response.isSuccessful) {
                response.body()?.let {
                    onImageListLoaded(it.data.list)
                }
            }
        }

        override fun onFailure(call: Call<LoadImageResponse>, t: Throwable) {
            Log.i("Load Image Response","onFailure: ${t.message}")
        }
    })
}

// Function to create a square bitmap from a bitmap
private fun createSquareBitmap(bitmap: Bitmap): Bitmap {
    val size = min(bitmap.width, bitmap.height)
    val startX = (bitmap.width - size) / 2
    val startY = (bitmap.height - size) / 2

    //Rotate 90
    val matrix = Matrix()
    matrix.postRotate(90f)
    return Bitmap.createBitmap(bitmap, startX, startY, size, size, matrix, true)
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