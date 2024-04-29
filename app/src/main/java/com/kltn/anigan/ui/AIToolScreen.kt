package com.kltn.anigan.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.api.LoadImageApi
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.domain.enums.ImageType
import com.kltn.anigan.domain.response.LoadImageResponse
import com.kltn.anigan.ui.shared.components.GenerateSetting
import com.kltn.anigan.ui.shared.layouts.Header
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.BitmapUtils.Companion.rotate90
import com.kltn.anigan.utils.UriUtils.Companion.saveBitmapAndGetUri
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

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

            // Save squareBitmap to file and get the new URI
            val bitmapAfterRotate = rotate90(bitmap)
            val newUri = saveBitmapAndGetUri(context, bitmapAfterRotate)
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
            .defaultMinSize(minHeight = 250.dp)
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
                contentScale = ContentScale.Inside
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