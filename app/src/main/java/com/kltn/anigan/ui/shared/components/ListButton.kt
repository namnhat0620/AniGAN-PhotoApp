package com.kltn.anigan.ui.shared.components

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.domain.enums.EditType
import com.kltn.anigan.routes.Routes
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
public fun FuncButton(imageId: Int, text: String,  onClick: () -> Unit = {}, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "edit_btn",
            modifier.size(30.dp)
        )
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ListButton(uri: Uri, navController: NavController, onEditResult: (Uri) -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity

    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val newUri = UCrop.getOutput(data!!)
            if (newUri != null) {
                navController.navigate("${Routes.EDIT_SCREEN.route}?uri=$newUri&editType=${EditType.DEFAULT.type}")
            }
        }
    }

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            FuncButton2(
                R.drawable._crop, "Crop",
                onClick = {
                    val destinationFileName = "image_${System.currentTimeMillis()}.jpg"
                    val destinationUri = Uri.fromFile(File(context.cacheDir, destinationFileName))
                    val uCrop = UCrop.of(uri, destinationUri)
                        .withAspectRatio(16F, 9F)

                    val uCropIntent = uCrop.getIntent(context)
                    cropLauncher.launch(uCropIntent)
                },
                modifier = Modifier
            )
            FuncButton2(
                R.drawable._text, "Text",
                modifier = Modifier
            )
            FuncButton2(
                R.drawable._emoji, "Emoji",
                modifier = Modifier
            )
            FuncButton2(
                R.drawable._filter, "Filters",
                onClick = {
                    navController.navigate(Routes.FILLTER_TOOL.route)
                },
                modifier = Modifier
            )
            FuncButton2(
                R.drawable._rotate, "Rotate\nFlip",
                modifier = Modifier
            )
        }

    }
}

@Composable
private fun FuncButton2(imageId: Int, text: String,  onClick: () -> Unit = {}, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "edit_btn",
            modifier.size(23.dp)
        )
        Spacer(modifier = modifier.height(5.dp))
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 15.sp
        )
    }
}