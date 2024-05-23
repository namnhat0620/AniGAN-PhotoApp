package com.kltn.anigan.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.kltn.anigan.R
import com.kltn.anigan.api.UploadApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.request.UploadRequestBody
import com.kltn.anigan.domain.response.UploadUserImageResponse
import com.kltn.anigan.ui.shared.components.GenerateSetting
import com.kltn.anigan.ui.shared.components.Title
import com.kltn.anigan.utils.BitmapUtils.Companion.convertBitmap2ByteArray
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUri
import com.kltn.anigan.utils.UriUtils.Companion.encodeUri
import com.kltn.anigan.utils.UriUtils.Companion.getFileName
import com.kltn.anigan.utils.UriUtils.Companion.saveBitmapAndGetUri
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun AIToolScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current
    val url = viewModel.url.value
    val bitmap = viewModel.bitmap
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(bitmap, url) {
        if (bitmap != null && url.isEmpty()) {
            launch {
                generateImageFromBitmap(context, viewModel) {
                    isLoading = it
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Header(navController, viewModel)
            InsertImage(viewModel, isLoading) {
                isLoading = it
            }
        }

        Column {
            Title(text1 = "Style", text2 = "")
            RefLibrary(viewModel)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
            {
                GenerateSetting(
                    url.isNotEmpty(),
                    navController
                )
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("HardwareIds")
@Composable
private fun InsertImage(
    viewModel: DocsViewModel,
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit
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
            val bitmap = getBitmapFromUri(uri, context)
                ?: return@rememberLauncherForActivityResult
            viewModel.bitmap = bitmap
            val newUri = saveBitmapAndGetUri(context, bitmap)
            viewModel.uri.value = newUri.toString()
            if (newUri != null) {
                // Upload file to server
                generateImage(context, viewModel) {
                    onLoadingChange(it)
                }
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

    Row(
        Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.background_gray))
            .defaultMinSize(minHeight = 250.dp)
            .fillMaxHeight(0.6f)
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
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (viewModel.url.value.isNotEmpty()) {
            GlideImage(
                model = viewModel.url.value,
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.insert_img),
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

//private fun getRefImage(onImageListLoaded: (List<ImageClassFromInternet>) -> Unit) {
//    LoadImageApi().getRefImage(ImageType.REFERENCE_IMAGE.type).enqueue(object: Callback<LoadImageResponse>{
//        override fun onResponse(
//            call: Call<LoadImageResponse>,
//            response: Response<LoadImageResponse>
//        ) {
//            if(response.isSuccessful) {
//                response.body()?.let {
//                    onImageListLoaded(it.data.list)
//                }
//            }
//        }
//
//        override fun onFailure(call: Call<LoadImageResponse>, t: Throwable) {
//            Log.i("Load Image Response","onFailure: ${t.message}")
//        }
//    })
//}

@Composable
private fun Header(
    navController: NavController,
    viewModel: DocsViewModel
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.bitmap = getBitmapFromUri(it, context)
                viewModel.url.value = ""
            }
        })

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_back),
                contentDescription = "icon_change_image",
                Modifier
                    .size(20.dp)
                    .clickable {
                        navController.popBackStack()
                    },
            )
            Spacer(Modifier.width(15.dp))
            //Icon notification
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .padding(0.dp)
                    .height(40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_energy_savings_leaf_24),
                    contentDescription = ""
                )
                Spacer(Modifier.width(1.dp))
                Text(
                    text = "${viewModel.numberOfGeneration.intValue}",
                    color = Color.White
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.outline_image_24),
            contentDescription = "icon_change_image",
            Modifier
                .size(35.dp)
                .clickable {
                    galleryLauncher.launch("image/*")
                }
        )
    }
}

@SuppressLint("Recycle")
private fun generateImage(
    context: Context,
    viewModel: DocsViewModel,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    val uri = if (viewModel.uri.value.equals(Uri.EMPTY)) {
        Uri.EMPTY
    } else {
        encodeUri(viewModel.uri.value)
    }

    if (uri == Uri.EMPTY) {
        Toast.makeText(context, "Choose an image first!", Toast.LENGTH_LONG).show()
        return
    }

    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
        uri, "r", null
    ) ?: return

    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
    val file = File(
        context.cacheDir,
        context.contentResolver.getFileName(uri)
    )
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)

    val body = UploadRequestBody(file, "image")
    UploadApi().uploadImage(
        MultipartBody.Part.createFormData(
            "file",
            file.name,
            body
        ),
        "".toRequestBody("text/plain".toMediaTypeOrNull()),
    ).enqueue(object : Callback<UploadUserImageResponse> {
        override fun onResponse(
            call: Call<UploadUserImageResponse>,
            response: Response<UploadUserImageResponse>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    viewModel.url.value = it.url
                }
                onLoadingChange(false)
            } else {
                // Handle error response
                val errorMessage = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error parsing error message"
                }

                // Parse error message from JSON if needed
                val jsonObj = JSONObject(errorMessage)
                val message = jsonObj.optString("message", "Unknown error")

                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                onLoadingChange(false)
            }
        }

        override fun onFailure(call: Call<UploadUserImageResponse>, t: Throwable) {
            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                .show()
            onLoadingChange(false)
        }
    })
}

@SuppressLint("Recycle")
private fun generateImageFromBitmap(
    context: Context,
    viewModel: DocsViewModel,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    val byteArray = convertBitmap2ByteArray(viewModel.bitmap!!)

    // Create a RequestBody from the ByteArray
    val body = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)

    UploadApi().uploadImage(
        MultipartBody.Part.createFormData(
            "file",
            "image_${System.currentTimeMillis()}.jpg",
            body
        ),
        "".toRequestBody("text/plain".toMediaTypeOrNull()),
    ).enqueue(object : Callback<UploadUserImageResponse> {
        override fun onResponse(
            call: Call<UploadUserImageResponse>,
            response: Response<UploadUserImageResponse>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    viewModel.url.value = it.url
                }
                onLoadingChange(false)

            } else {
                // Handle error response
                val errorMessage = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error parsing error message"
                }

                // Parse error message from JSON if needed
                val jsonObj = JSONObject(errorMessage)
                val message = jsonObj.optString("message", "Unknown error")

                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                onLoadingChange(false)
            }
        }

        override fun onFailure(call: Call<UploadUserImageResponse>, t: Throwable) {
            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                .show()
            onLoadingChange(false)
        }
    })
}

@Composable
private fun RefLibrary(viewModel: DocsViewModel) {
    val listRef = arrayListOf(
        R.drawable.face_paint_512_v2,
        R.drawable.face_paint_512_v1,
        R.drawable.paprika,
        R.drawable.celeba_distill,
    )

    LazyRow(
        modifier = Modifier.padding(vertical = 15.dp)
    ) {
        itemsIndexed(listRef) { index, it ->
            Image(
                painter = painterResource(id = it),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(100.dp)
                    .clickable {
                        viewModel.reference.intValue = index
                    }
                    .graphicsLayer {
                        alpha = if (index == viewModel.reference.intValue) 0.5f else 1f
                    }
            )
        }
    }
}