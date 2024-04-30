package com.kltn.anigan.ui

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.api.LoadImageApi
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.domain.enums.ImageType
import com.kltn.anigan.domain.response.LoadImageResponse
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.PhotoLibrary
import com.kltn.anigan.ui.shared.components.Title
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MainScreen(navController: NavController?) {
    var userImages by remember { mutableStateOf<List<ImageClassFromInternet>>(emptyList()) }
    var aniganImages by remember { mutableStateOf<List<ImageClassFromInternet>>(emptyList()) }

    getImage(type = ImageType.USER_IMAGE.type) { updatedList ->
        // Update the contents of the list variable with the data returned from getRefImage
        userImages = updatedList
    }

    getImage(type = ImageType.ANIGAN_IMAGE.type) { updatedList ->
        // Update the contents of the list variable with the data returned from getRefImage
        aniganImages = updatedList
    }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.black))
            .fillMaxSize()
            .padding(horizontal = 12.dp)
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

            Title(text1 = "Edit Your Photos", text2 = "")
            PhotoLibrary(userImages)

            Title(text1 = "History", text2 = "")
            PhotoLibrary(aniganImages)
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
        Text(text = "Anigan", fontSize = 25.sp, color = Color.White)
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
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { newUri ->
            if (newUri != null && navController != null) {
                navController.navigate("${Routes.EDIT_SCREEN.route}?uri=$newUri")
            }
        }
    )

    Row (
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
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
}



@Composable
private fun Banner(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.banner),
        contentDescription = "banner",
        modifier.padding(vertical = 30.dp))
}

private fun getImage(type: Int, onImageListLoaded: (List<ImageClassFromInternet>) -> Unit) {
    LoadImageApi().getRefImage(type).enqueue(object:
        Callback<LoadImageResponse> {
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

