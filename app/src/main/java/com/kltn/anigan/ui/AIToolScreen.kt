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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.api.UploadApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.request.UploadRequestBody
import com.kltn.anigan.domain.response.UploadUserImageResponse
import com.kltn.anigan.ui.shared.components.GenerateSetting
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.UriUtils.Companion.encodeUri
import com.kltn.anigan.utils.UriUtils.Companion.getFileName
import com.kltn.anigan.utils.UriUtils.Companion.saveBitmapAndGetUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
    val isLoading = viewModel.isLoading.value
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Header(
                setCapturedImageUri = { newUri ->
                    if (newUri != null) {
//                        capturedImageUri = newUri
                    }
                },
                navController
            )
            InsertImage(viewModel)
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

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
        {
            GenerateSetting(
                viewModel.url.value,
                "",
                isLoading,
                navController
            )
        }
    }
}

@Composable
private fun InsertImage(
    viewModel: DocsViewModel
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
            val bitmap = BitmapUtils.getBitmapFromUri(uri, context)
                ?: return@rememberLauncherForActivityResult
            viewModel.bitmap.value = bitmap
            // Save squareBitmap to file and get the new URI
//            val bitmapAfterRotate = rotate90(bitmap)
            val newUri = saveBitmapAndGetUri(context, bitmap)
            viewModel.uri.value = newUri.toString()
            if (newUri != null) {
                // Upload file to server
                generateImage(context, viewModel)
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
            .heightIn(max = 550.dp)
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
        if (viewModel.isLoading.value) {
            CircularProgressIndicator()
        } else if (viewModel.url.value.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(viewModel.url.value),
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
    setCapturedImageUri: (Uri?) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            setCapturedImageUri(uri)
        })

    Row(
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            modifier
                .size(17.dp)
                .clickable {
                    navController.popBackStack()
                },
        )
        Image(
            painter = painterResource(id = R.drawable.outline_image_24),
            contentDescription = "icon_change_image",
            modifier
                .size(30.dp)
                .clickable { galleryLauncher.launch("image/*") }
        )
    }
}

@SuppressLint("Recycle")
private fun generateImage(
    context: Context,
    viewModel: DocsViewModel
) {
    viewModel.isLoading.value = true
    val uri = encodeUri(viewModel.uri.value)

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
            response.body()?.let {
                Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                viewModel.url.value = it.url
            }
            viewModel.isLoading.value = false
        }

        override fun onFailure(call: Call<UploadUserImageResponse>, t: Throwable) {
            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                .show()
            viewModel.isLoading.value = true
        }
    })
}

