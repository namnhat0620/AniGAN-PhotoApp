package com.kltn.anigan.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.api.LoadImageApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.domain.enums.ImageType
import com.kltn.anigan.domain.response.LoadImageResponse
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.PhotoLibrary
import com.kltn.anigan.ui.shared.components.Title
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUri
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MainScreen(navController: NavController?, viewModel: DocsViewModel) {
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
    ) {
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
        ) {
            Header()
            ListButton(navController = navController, viewModel)
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
    Row(
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
    ) {
        Text(text = "Anigan", fontSize = 25.sp, color = Color.White)
    }
}

@Composable
private fun FuncButton(imageId: Int, text: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            Modifier.size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF202020), // Circle color
                    center = center, // Center of the circle
                    radius = size.width / 2 // Radius of the circle
                )
            }
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "edit_btn",
                Modifier.size(25.dp)
            )
        }

        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ListButton(navController: NavController? = null, viewModel: DocsViewModel) {
    var route by remember {
        mutableStateOf(Routes.MAIN_SCREEN)
    }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it?.let {
                viewModel.bitmap.value = getBitmapFromUri(context = context, uri = it)
                viewModel.uri.value = it.toString()
                navController?.navigate("$route?uri=$it")
            }
        }
    )

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        FuncButton(R.drawable.baseline_add_24, "Edit",
            onClick = {
                route = Routes.EDIT_SCREEN
                galleryLauncher.launch("image/*")
            })
        FuncButton(R.drawable.baseline_auto_awesome_24, "AI Tools",
            onClick = {
                navController?.navigate(Routes.AI_TOOLS.route)
            })
        FuncButton(R.drawable.baseline_brush_24, "Brush",
            onClick = {
                route = Routes.HAIR_SCREEN
                galleryLauncher.launch("image/*")
            })
        FuncButton(R.drawable.baseline_text_fields_24, "Text",
            onClick = {
                route = Routes.ADD_TEXT
                galleryLauncher.launch("image/*")
            })
        FuncButton(R.drawable.baseline_auto_fix_high_24, "Filter",
            onClick = {
                route = Routes.FILTER_TOOL
                galleryLauncher.launch("image/*")
            })
    }
}


@Composable
private fun Banner(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.banner),
        contentDescription = "banner",
        modifier.padding(vertical = 30.dp)
    )
}

private fun getImage(type: Int, onImageListLoaded: (List<ImageClassFromInternet>) -> Unit) {
    LoadImageApi().getRefImage(type).enqueue(object :
        Callback<LoadImageResponse> {
        override fun onResponse(
            call: Call<LoadImageResponse>,
            response: Response<LoadImageResponse>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    onImageListLoaded(it.data.list)
                }
            }
        }

        override fun onFailure(call: Call<LoadImageResponse>, t: Throwable) {
            Log.i("Load Image Response", "onFailure: ${t.message}")
        }
    })
}

