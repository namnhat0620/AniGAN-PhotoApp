package com.kltn.anigan.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.ui.shared.components.ListButton
import com.kltn.anigan.ui.shared.layouts.Header1
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.min

@Composable
fun FillTextScreen(navController: NavController,uri: String?) {
    val capturedImageUri = Uri.parse(encodeUri(uri ?: ""));

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize(),

    ){
        Column {
            Header1(navController, capturedImageUri)

            if(capturedImageUri.path?.isNotEmpty() == true) {
                Image(
                    painter = rememberImagePainter(capturedImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
//                    colorFilter = ColorFilter.tint(Color.Green, blendMode = BlendMode.Darken)
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.insert_img) ,
                    contentDescription = "Insert Image"
                )
            }

            ListButton()
        }
    }

}

@Composable
private fun InsertImage(capturedImageUri: Uri,
                        setCapturedImageUri: (Uri) -> Unit,
                        modifier: Modifier = Modifier)
{

    Row (
        modifier
            .background(color = colorResource(id = R.color.background_gray)),
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

private fun encodeUri(uriString: String): String {
    val uri = Uri.parse(uriString)
    val encodedUriString = Uri.encode(uri.lastPathSegment)
    return uriString.replace(uri.lastPathSegment ?: "", encodedUriString)
}




