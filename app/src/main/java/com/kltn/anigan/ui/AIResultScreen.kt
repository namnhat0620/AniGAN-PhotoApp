package com.kltn.anigan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.kltn.anigan.R
import com.kltn.anigan.api.TransformApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.enums.ResolutionOption
import com.kltn.anigan.domain.request.TransformRequest
import com.kltn.anigan.domain.response.TransformResponse
import com.kltn.anigan.ui.shared.components.ListButton
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUrl
import com.kltn.anigan.utils.DataStoreManager
import com.kltn.anigan.utils.HardwareUtils
import com.kltn.anigan.utils.UriUtils.Companion.saveBitmapAndGetUri
import com.kltn.anigan.utils.UriUtils.Companion.saveImageFromUrl
import com.kltn.anigan.utils.UriUtils.Companion.shareImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun AIResultScreen(
    navController: NavController,
    viewModel: DocsViewModel
) {
    val context = LocalContext.current
    val focusURL = viewModel.resultUrl.value
    val shareImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val url = viewModel.url.value
    var isLoading by remember { mutableStateOf(false) }

    if (url.isEmpty()) {
        navController.popBackStack()
        return
    }
    LaunchedEffect(Unit) {
        transformImage(context, viewModel, navController) {
            isLoading = it
        }
    }

    Column(
        Modifier.background(Color.Black).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Header(navController = navController)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isLoading) {
                GlideImage(
                    model = viewModel.bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Inside
                )
            } else {
                    CircularProgressIndicator()
            }
        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Column(
                Modifier.background(colorResource(id = R.color.background_gray))
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    CustomButton(
                        drawableId = R.drawable.icon_share,
                        text = "Share",
                        backgroundColorId = R.color.background_share_btn,

                        modifier = Modifier
                            .clickable(
                                enabled = !isLoading
                            ) {
                                onShareButtonClick(context, shareImageLauncher, focusURL)
                            }
                            .alpha(if (isLoading) 0.4f else 1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CustomButton(
                        drawableId = R.drawable.icon_download,
                        text = "Download",
                        backgroundColorId = R.color.background_blue,
                        Modifier
                            .clickable(
                                enabled = !isLoading
                            ) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    saveBitmapAndGetUri(context, viewModel.bitmap!!)
                                }
                                Toast
                                    .makeText(context, "Successfully", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .alpha(if (isLoading) 0.4f else 1f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                ListButton(navController, viewModel, isLoading)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CustomButton(
    drawableId: Int,
    text: String,
    backgroundColorId: Int,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier
            .width(160.dp)
            .height(46.dp),
        colors = ButtonDefaults.buttonColors(colorResource(id = backgroundColorId))
    ) {
        Row(
            modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                modifier.size(16.dp)
            )
            Spacer(modifier = modifier.width(6.dp))
            Text(text = text, fontSize = 14.sp)
        }
    }
}

@Composable
private fun Header(navController: NavController) {
    Row(
        Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            Modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(20.dp)
                .clickable {
                    navController.popBackStack()
                },
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("Recycle", "HardwareIds")
private fun transformImage(
    context: Context,
    viewModel: DocsViewModel,
    navController: NavController,
    onLoadingChange: (Boolean) -> Unit
) {
    val sourceUrl = viewModel.url.value
    onLoadingChange(true)

    try {
        TransformApi().transformImage(
            if (viewModel.accessToken.value != "") "Bearer ${viewModel.accessToken.value}"
            else "",
            TransformRequest(
                sourceImg = sourceUrl,
                referenceId = viewModel.reference.intValue,
                mobileId = HardwareUtils.getMobileId(context),
                resolutionOption = viewModel.resolutionOption.value
            )
        ).enqueue(object : Callback<TransformResponse> {
            override fun onResponse(
                call: Call<TransformResponse>,
                response: Response<TransformResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                        viewModel.resultUrl.value = it.data.url

                        if (viewModel.resultUrl.value.isNotEmpty()) {
                            GlobalScope.launch(Dispatchers.IO) {
                                viewModel.bitmap =
                                    getBitmapFromUrl(
                                        context = context,
                                        urlString = viewModel.resultUrl.value,
                                        accessToken = viewModel.accessToken.value
                                    )
                                viewModel.url.value = viewModel.resultUrl.value

                                withContext(Dispatchers.Default) {
                                    DataStoreManager.getNoOfGeneration(context)
                                        .collect { noOfGeneration ->
                                            noOfGeneration?.let {
                                                DataStoreManager.saveNoOfGeneration(
                                                    context,
                                                    (Integer.parseInt(noOfGeneration) - 1).toString()
                                                )
                                            }
                                        }
                                }
                            }
                        }
                        onLoadingChange(false)
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
                    navController.popBackStack()
                    onLoadingChange(false)
                }
            }

            override fun onFailure(call: Call<TransformResponse>, t: Throwable) {
                Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                    .show()
                navController.popBackStack()
                onLoadingChange(false)
            }
        })
    } catch (e: IOException) {
        Toast.makeText(context, "Fail by ${e.message!!}!", Toast.LENGTH_LONG)
            .show()
        navController.popBackStack()
        onLoadingChange(false)
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun onShareButtonClick(
    context: Context,
    shareImageLauncher: ActivityResultLauncher<Intent>,
    imageUrl: String
) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Download the image and save it to a file
            val imagePath = saveImageFromUrl(context, imageUrl)

            // Share the image
            shareImage(shareImageLauncher, imagePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}