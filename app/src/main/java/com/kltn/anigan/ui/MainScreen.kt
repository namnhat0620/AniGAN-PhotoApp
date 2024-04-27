package com.kltn.anigan.ui

import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.routes.Routes

@Composable
fun MainScreen(navController: NavController) {
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
            ListButton(navController = navController)
        }
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .verticalScroll(rememberScrollState())
        ) {
            Banner()

//            Title(text1 = "Edit Your Photos", text2 = "All photos >")
//            val defaultLibrary = listOf<ImageClass>(
//                ImageClass()
//            )
//            PhotoLibrary(defaultLibrary)

//            Title(text1 = "Magazine Collage", text2 = "See all >")
//            PhotoLibrary(defaultLibrary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Column(
        modifier = Modifier.background(color = colorResource(id = R.color.black))
    ){
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
        ) {
            Header()
            ListButton()
        }
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .verticalScroll(rememberScrollState())
        ) {
            Banner()

//            Title(text1 = "Edit Your Photos", text2 = "All photos >")
//            val defaultLibrary = listOf<ImageClass>(
//                ImageClass()
//            )
//            PhotoLibrary(defaultLibrary)

//            Title(text1 = "Magazine Collage", text2 = "See all >")
//            PhotoLibrary(defaultLibrary)
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
    ){
        //Icon menu
        Image(
            painter = painterResource(id = R.drawable.icon_menu),
            contentDescription = "icon_menu",
            modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(17.dp),
            )

        //Icon notification
        Image(
            painter = painterResource(id = R.drawable.icon_bell),
            contentDescription = "icon_menu",
            modifier
                .padding(start = 17.dp, top = 16.dp)
                .size(17.dp)
        )

    }
}

@Composable
private fun FuncButton(imageId: Int, text: String,  onClick: () -> Unit = {}, modifier: Modifier) {



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "edit_btn",
            modifier.size(50.dp)
            )
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ListButton(modifier: Modifier = Modifier, navController: NavController ?= null) {

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            newUri ->
            if (newUri != null) {
                navController?.navigate("${Routes.FILLTER_TOOL.route}?uri=${newUri}")
//                capturedImageUri = newUri;
            }
        }
    )
    Row (
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    )
    {
        FuncButton(R.drawable.edit_btn, "Edit",
            onClick = {
                galleryLauncher.launch("image/*")
            },
            modifier = modifier)
        FuncButton(R.drawable.ai_tool_btn, "AI Tools",
            onClick = {
                navController?.navigate(Routes.AI_TOOLS.route)
            },
            modifier = modifier)
        FuncButton(R.drawable.collage_btn, "Collage",
            onClick = {
                navController?.navigate(Routes.COLLAGE_TOOL.route)
            },
            modifier = modifier)
        FuncButton(R.drawable.bg_remove_btn, "Bg\nremover",
            onClick = {
                navController?.navigate(Routes.BG_REMOVER_TOOL.route)
            },
            modifier = modifier)
        FuncButton(R.drawable.more_btn, "More",
            modifier = modifier)
    }

    Image(
        painter = rememberImagePainter(capturedImageUri),
        contentDescription = null
    )
}



@Composable
private fun Banner(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.banner),
        contentDescription = "banner",
        modifier.padding(
            horizontal = 12.dp,
            vertical = 30.dp))
}

