package com.kltn.anigan.ui

import android.Manifest
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.ui.shared.components.ListButton
import com.kltn.anigan.ui.shared.layouts.Header1
import androidx.compose.foundation.layout.Row
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun CollageScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize(),


    ){
        Column {
//            Header1(navController)

            InsertImage(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) )

//            ListButton()
        }
    }

}

@Composable
private fun InsertImage(modifier: Modifier = Modifier)
{

    Row (
        modifier
            .background(color = colorResource(id = R.color.background_gray)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,


    ){

            Image(
                painter = painterResource(id = R.drawable.insert_img) ,
                contentDescription = "Insert Image" ,

            )
    }
}


