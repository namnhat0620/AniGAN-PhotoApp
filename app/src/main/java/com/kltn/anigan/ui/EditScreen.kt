package com.kltn.anigan.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.domain.enums.EditType
import com.kltn.anigan.ui.shared.components.ListButton
import com.kltn.anigan.utils.UriUtils.Companion.encodeUri

@Preview
@Composable
fun EditScreen(
    navController: NavController = NavController(LocalContext.current),
    uri: String? = "",
    editType: String? = EditType.DEFAULT.type
) {
    if(uri.isNullOrEmpty()) return
    val capturedImageUri = Uri.parse(encodeUri(uri ?: ""))

    var isDefault by remember {
        mutableStateOf<Boolean>(EditType.DEFAULT.type == editType)
    }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxWidth(),
        ) {
        Column {
            var colorFilter by remember { mutableStateOf<ColorFilter?>(null) }

            //Header
            AnimatedVisibility(visible = isDefault) {
                Header(navController = navController)
            }

            //Image field
            if (capturedImageUri.path?.isNotEmpty() == true) {
                Image(
                    painter = rememberImagePainter(capturedImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
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
                    EditType.DEFAULT.type -> ListButton()
                    EditType.FILTER.type -> FillterScreen(navController, capturedImageUri)
                }
            }

        }
    }


}