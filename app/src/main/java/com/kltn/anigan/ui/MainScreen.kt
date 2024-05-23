package com.kltn.anigan.ui

import android.content.Context
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kltn.anigan.api.RefreshTokenApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.response.LoginResponse
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.PhotoLibrary
import com.kltn.anigan.ui.shared.components.Title
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUri
import com.kltn.anigan.utils.DataStoreManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MainScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                DataStoreManager.getRefreshToken(context).collect {
                    it?.let {
                        if (it.isNotEmpty()) {
                            refreshToken(it, context, viewModel)
                        }
                    }
                }
            }

            launch {
                viewModel.loadMoreUserImages()
            }

            launch {
                viewModel.loadMoreAniganImages()
            }
        }
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
            Header(navController = navController, viewModel)
            ListButton(navController = navController, viewModel)
        }
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .verticalScroll(rememberScrollState())
        ) {
            Banner()

            Title(text1 = "Edit Your Photos", text2 = "")
            PhotoLibrary(viewModel.userImages, viewModel, navController) {
                viewModel.loadMoreUserImages()
            }

            Title(text1 = "History", text2 = "")
            PhotoLibrary(viewModel.aniganImages, viewModel, navController) {
                viewModel.loadMoreAniganImages()
            }
        }
    }
}

@Composable
private fun Header(navController: NavController?, viewModel: DocsViewModel) {
    Row(
        Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Anigan", fontSize = 25.sp, color = Color.White)
        //Icon notification
        OutlinedButton(
            onClick = {
                if (viewModel.username.value.isEmpty()) {
                    navController?.navigate(Routes.LOGIN.route)
                } else {
                    navController?.navigate(Routes.PROFILE.route)
                }
            }
        ) {
            if (viewModel.username.value.isNotEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.round_account_circle_24),
                    contentDescription = ""
                )
                Spacer(Modifier.width(1.dp))
                Text(text = viewModel.username.value, color = Color.White)
            } else {
                Text(text = "Login", color = Color.White)
            }
        }
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
                viewModel.bitmap = getBitmapFromUri(context = context, uri = it)
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

@OptIn(DelicateCoroutinesApi::class)
private fun refreshToken(token: String, context: Context, viewModel: DocsViewModel) {
    RefreshTokenApi().refresh("Bearer $token").enqueue(object :
        Callback<LoginResponse> {
        override fun onResponse(
            call: Call<LoginResponse>,
            response: Response<LoginResponse>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    viewModel.accessToken.value = it.access_token
                    viewModel.refreshToken.value = it.refresh_token

                    GlobalScope.launch {
                        DataStoreManager.getUsername(context).collect { username ->
                            username?.let {
                                viewModel.username.value = username
                            }
                        }

                        DataStoreManager.getNoOfGeneration(context).collect { numOfGeneration ->
                            numOfGeneration?.let {
                                viewModel.numberOfGeneration.intValue =
                                    Integer.parseInt(numOfGeneration)
                            }
                        }
                    }
                }
            } else {
                // Clear login info
                GlobalScope.launch {
                    DataStoreManager.clearUsername(context)
                    DataStoreManager.clearRefreshToken(context)
                    DataStoreManager.clearNoOfGeneration(context)
                }
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            Log.i("Load Image Response", "onFailure: ${t.message}")
        }
    })
}