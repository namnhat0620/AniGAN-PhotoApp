package com.kltn.anigan.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.domain.enums.EditType
import com.kltn.anigan.ui.shared.components.ListButton
import com.kltn.anigan.utils.UriUtils
import com.kltn.anigan.utils.UriUtils.Companion.encodeUri

@Preview
@Composable
fun EditScreen(
    navController: NavController = NavController(LocalContext.current),
    uri: String? = "",
    editType: String? = EditType.DEFAULT.type
) {
    if(uri.isNullOrEmpty()) return

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.parse(encodeUri(uri)))
    }


    var isDefault by remember {
        mutableStateOf<Boolean>(EditType.DEFAULT.type == editType)
    }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxWidth(),
        ) {

        //Header
        AnimatedVisibility(visible = isDefault) {
            Header(navController = navController, uri = capturedImageUri, fileName = "image_${System.currentTimeMillis()}.jpg")
        }

        Column {
            val colorFilter by remember { mutableStateOf<ColorFilter?>(null) }


            //Image field
            if (capturedImageUri.path?.isNotEmpty() == true) {
                Image(
                    painter = rememberImagePainter(capturedImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.Inside,
                    colorFilter = colorFilter
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.insert_img),
                    contentDescription = "Insert Image"
                )
            }

            //Footer
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                when (editType) {
                    EditType.DEFAULT.type -> ListButton(uri = capturedImageUri, navController = navController) { newUri ->
                        capturedImageUri = newUri
                    }
                    EditType.FILTER.type -> FillterScreen(navController, capturedImageUri)
                }
            }

        }
    }
}

@Composable
private fun Header(
    navController: NavController,
    uri: Uri,
    fileName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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

        //Icon notification
        OutlinedButton(
            onClick = {
                UriUtils.saveUriToLibrary(context, uri, fileName)
                Toast.makeText(context, "Successfully!", Toast.LENGTH_LONG).show()
            }
        ) {
            Text(text = "Save", color = Color.White)
        }

    }
}