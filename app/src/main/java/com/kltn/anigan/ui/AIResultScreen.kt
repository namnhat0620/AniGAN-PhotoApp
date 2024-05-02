package com.kltn.anigan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kltn.anigan.R
import com.kltn.anigan.api.TransformApi
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.domain.request.TransformRequest
import com.kltn.anigan.domain.response.TransformResponse
import com.kltn.anigan.ui.shared.components.PhotoLibrary
import com.kltn.anigan.utils.UriUtils.Companion.saveImageFromUrl
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun AIResultScreen(
    navController: NavController,
    num: Int?,
    uri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var resultList by remember { mutableStateOf<List<ImageClassFromInternet>>(emptyList()) }
    var focusURL by remember { mutableStateOf("") }
    val shareImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){}

    Column(
        modifier.background(Color.Black)
    ) {
        Header(navController = navController)

        if (num == null || uri == null) return
        LaunchedEffect(Unit) {
            transformImage(uri, context) {
//                    resultList += ImageClassFromInternet(
//                        it.image_id,
//                        it.url,
//                        ImageType.ANIGAN_IMAGE.type
//                    )
//                    if(resultList.size == 1) {
                focusURL = it.url
//                    }
            }
        }

        if (focusURL.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(focusURL),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }
        }

        PhotoLibrary(itemList = resultList) {
            focusURL = it
        }

        Spacer(modifier = modifier.height(20.dp))

        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                CustomButton(
                    drawableId = R.drawable.icon_share,
                    text = "Share",
                    backgroundColorId = R.color.background_gray,
                    modifier = modifier.clickable(
                        enabled = focusURL.isNotEmpty()
                    ) {
//                        GlobalScope.launch(Dispatchers.Main) {
//                            val bitmap = getBitmapFromUrl(focusURL, context) ?: return@launch
//                            // Use the bitmap here on the main UI thread
//                            val sharePhotoContent = sharePhotoFB(bitmap)
//                            ShareDialog.show(activity, sharePhotoContent)
//                        }
                        onShareButtonClick(context, shareImageLauncher, focusURL)
                    }
                )
                Spacer(modifier = modifier.width(10.dp))
                CustomButton(
                    drawableId = R.drawable.icon_download,
                    text = "Download",
                    backgroundColorId = R.color.background_blue,
                    modifier.clickable {
                        saveImageFromUrl(focusURL)
                        Toast.makeText(context, "Successfully!", Toast.LENGTH_LONG).show()
                    }
                )
            }
            Spacer(modifier = modifier.height(20.dp))
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
        onClick = { /*TODO*/ },
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
            Text(text = text, fontSize = 18.sp)
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
                .size(17.dp)
                .clickable {
                    navController.popBackStack()
                },
        )
    }
}

@SuppressLint("Recycle")
private fun transformImage(
    url: String,
    context: Context,
    setImageFromResponse: (ImageClassFromInternet) -> Unit
) {
    TransformApi().transformImage(
        TransformRequest(sourceImg = url)
    ).enqueue(object : Callback<TransformResponse> {
        override fun onResponse(
            call: Call<TransformResponse>,
            response: Response<TransformResponse>
        ) {
            response.body()?.let {
                Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                setImageFromResponse(it.data)
            }
        }

        override fun onFailure(call: Call<TransformResponse>, t: Throwable) {
            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                .show()
        }
    })
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
            shareImage(context, shareImageLauncher, imagePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun saveImageFromUrl(context: Context, imageUrl: String): String {
    val url = URL(imageUrl)

    // Create a directory for saving the image
    val directory = File(context.externalCacheDir, "")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    // Extract the filename from the URL
    val filename = imageUrl.substringAfterLast("/")

    // Build the path to save the image
    val savePath = File(directory, filename)

    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
    connection.doInput = true
    connection.connect()
    val input = connection.inputStream

    val fileOutputStream = FileOutputStream(savePath)

    val buffer = ByteArray(1024)
    var bytesRead: Int
    while (input.read(buffer).also { bytesRead = it } != -1) {
        fileOutputStream.write(buffer, 0, bytesRead)
    }

    fileOutputStream.flush()
    fileOutputStream.close()
    input.close()

    return savePath.absolutePath
}

private fun shareImage(context: Context, shareImageLauncher: ActivityResultLauncher<Intent>, imagePath: String) {
    val imageFile = File(imagePath)
    val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(shareIntent, "Share Image")
    shareImageLauncher.launch(chooser)
}