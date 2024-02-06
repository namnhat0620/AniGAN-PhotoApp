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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import coil.compose.rememberImagePainter
import com.kltn.anigan.ImageClass
import com.kltn.anigan.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
fun AIToolScreen() {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
        ) {
            Header()
            InsertImage()

            Title(text1 = "Style", text2 = "More >")
            val defaultLibrary = listOf<ImageClass>(
                ImageClass(R.drawable._3, "3"),
                ImageClass(R.drawable._8, "8"),
                ImageClass(R.drawable._28, "28"),
                ImageClass(R.drawable._009, "009"),
            )
            PhotoLibrary(defaultLibrary)
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
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
                .size(17.dp),
        )
        Row {
            Image(
                painter = painterResource(id = R.drawable.icon_change_image),
                contentDescription = "icon_change_image",
                modifier
                    .padding(start = 12.dp, top = 16.dp)
                    .size(17.dp),
            )

            //Icon notification
            Image(
                painter = painterResource(id = R.drawable.icon_library),
                contentDescription = "icon_library",
                modifier
                    .padding(start = 17.dp, top = 16.dp, end = 12.dp)
                    .size(17.dp)
            )
        }


    }
}

@Composable
private fun InsertImage(modifier: Modifier = Modifier) {
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

        var capturedImageUri by remember {
            mutableStateOf<Uri>(Uri.EMPTY)
        }

        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                capturedImageUri = uri
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
                        println("permissionLauncher.launch")
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )
        }
    }
}

@Composable
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}