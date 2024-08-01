package com.kltn.anigan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import com.kltn.anigan.api.LoginApi
import com.kltn.anigan.api.RefreshTokenApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.request.LoginRequestBody
import com.kltn.anigan.domain.response.LoginResponse
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.PhotoLibrary
import com.kltn.anigan.ui.shared.components.Title
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUri
import com.kltn.anigan.utils.DataStoreManager
import com.kltn.anigan.utils.HardwareUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MainScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.userImages.clear()
        viewModel.aniganImages.clear()
        coroutineScope {
            launch {
                DataStoreManager.getRefreshToken(context).collect {
                    if (true == it?.isNotEmpty()) {
                        refreshToken(it, context, viewModel)
                    } else {
                        loginAsTechnicalUser(context, viewModel)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                viewModel.loadMoreUserImages(HardwareUtils.getMobileId(context), viewModel)
            }

            launch {
                viewModel.loadMoreAniganImages(HardwareUtils.getMobileId(context), viewModel)
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

            if(viewModel.userImages.size > 0) {
                Title(text1 = "Edit Your Photos", text2 = "")
                PhotoLibrary(viewModel.userImages, viewModel, navController) {
                    viewModel.loadMoreUserImages(HardwareUtils.getMobileId(context), viewModel)
                }
            }

            if(viewModel.aniganImages.size > 0) {
                Title(text1 = "History", text2 = "")
                PhotoLibrary(viewModel.aniganImages, viewModel, navController) {
                    viewModel.loadMoreAniganImages(HardwareUtils.getMobileId(context), viewModel)
                }
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
                if (viewModel.username.isEmpty()) {
                    navController?.navigate(Routes.LOGIN.route)
                } else {
                    navController?.navigate(Routes.PROFILE.route)
                }
            }
        ) {
            if (viewModel.username.isNotEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.round_account_circle_24),
                    contentDescription = ""
                )
                Spacer(Modifier.width(1.dp))
                Text(text = viewModel.username, color = Color.White)
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
        FuncButton(R.drawable.baseline_auto_awesome_24, "Face2\nAnime",
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
                                viewModel.changeUsername(username)
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

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("Recycle")
private fun loginAsTechnicalUser(
    context: Context,
    viewModel: DocsViewModel
) {
    LoginApi().login(
        LoginRequestBody(username = "technicaluser", password = "123")
    ).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(
            call: Call<LoginResponse>,
            response: Response<LoginResponse>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                    viewModel.accessToken.value = it.access_token
                    viewModel.refreshToken.value = it.refresh_token
                    GlobalScope.launch {
                        DataStoreManager.saveRefreshToken(context, it.refresh_token)
                    }
                }
            } else {
                // Handle error response
                val errorMessage = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Error parsing error message"
                }

                // Parse error message from JSON if needed
                val jsonObj = JSONObject(errorMessage)
                val message = jsonObj.optString("message", "Unknown error")

                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                .show()
        }
    })
}