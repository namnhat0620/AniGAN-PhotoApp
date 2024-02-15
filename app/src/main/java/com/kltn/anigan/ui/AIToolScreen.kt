package com.kltn.anigan.ui

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.api.LoadImageApi
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.domain.LoadImageResponse
import com.kltn.anigan.ui.shared.components.GenerateSetting
import com.kltn.anigan.ui.shared.components.PhotoLibrary
import com.kltn.anigan.ui.shared.components.Title
import com.kltn.anigan.ui.shared.layouts.Header
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun AIToolScreen() {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize()
    ){

        var capturedImageUri by remember {
            mutableStateOf<Uri>(Uri.EMPTY)
        }

        var referenceImageUrl by remember {
            mutableStateOf<String?>(null)
        }

        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
        ) {
            Header()
            InsertImage(
                capturedImageUri,
                setCapturedImageUri = { newUri ->
                    capturedImageUri = newUri
                }
            )
            var list by remember { mutableStateOf<List<ImageClassFromInternet>>(emptyList()) }

            getRefImage { updatedList ->
                // Update the contents of the list variable with the data returned from getRefImage
                list = updatedList
            }

            Title(text1 = "Style", text2 = "More >")
            PhotoLibrary(itemList = list, setReferenceImageUrl = { url ->
                referenceImageUrl = url
            })
            GenerateSetting(
                capturedImageUri,
                referenceImageUrl,
                setCapturedImageUri = { newUri ->
                    capturedImageUri = newUri
                })
        }
    }
}

@Composable
private fun InsertImage(
    capturedImageUri: Uri,
    setCapturedImageUri: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.insert_image_AI_Tools))
            .background(color = colorResource(id = R.color.background)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ){
        val context = LocalContext.current
        val file = context.createImageFile()
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            context.packageName + ".provider",
            file
        )

        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                setCapturedImageUri(uri)
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

        if(capturedImageUri.path?.isNotEmpty() == true) {
            Image(painter = rememberImagePainter(capturedImageUri), contentDescription = null)
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.insert_img) ,
                contentDescription = "Insert Image",
                modifier = modifier.clickable {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                    if(permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
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
    LoadImageApi().getRefImage().enqueue(object: Callback<LoadImageResponse>{
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