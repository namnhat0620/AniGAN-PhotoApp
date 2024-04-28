package com.kltn.anigan.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
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
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.kltn.anigan.R
import com.kltn.anigan.api.UploadApi
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.domain.enums.ImageType
import com.kltn.anigan.domain.response.TransformResponse
import com.kltn.anigan.domain.request.UploadRequestBody
import com.kltn.anigan.ui.shared.components.PhotoLibrary
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUrl
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun AIResultScreen(
    navController: NavController,
    num: Int?,
    uri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity ?: return // Get the activity from the LocalContext
    var isLoading by remember { mutableStateOf(false) }
    var resultList by remember { mutableStateOf<List<ImageClassFromInternet>>(emptyList()) }
    var focusURL by remember { mutableStateOf("") }

    Column(
        modifier.background(Color.Black)
    ) {
        Header(navController = navController)

        if(num == null || uri == null) return
        LaunchedEffect(Unit) {
            for (i in 0..<num) {
                generateImage(Uri.parse(uri), i, context) { transformResponse ->
                    resultList += ImageClassFromInternet(
                        transformResponse.image_id,
                        transformResponse.url,
                        ImageType.ANIGAN_IMAGE.type
                    )
                    if(resultList.size == 1) {
                        focusURL = resultList[0].url
                    }
                }
            }
        }

        if(focusURL.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(focusURL),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )
        }
        else {
            Row(modifier = Modifier
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
                        GlobalScope.launch(Dispatchers.Main) {
                            val bitmap = getBitmapFromUrl(focusURL, context) ?: return@launch
                            // Use the bitmap here on the main UI thread
                            val sharePhotoContent = sharePhotoFB(bitmap)
                            ShareDialog.show(activity, sharePhotoContent)
                        }
                    }
                )
                Spacer(modifier = modifier.width(10.dp))
                CustomButton(
                    drawableId = R.drawable.icon_download,
                    text = "Download",
                    backgroundColorId = R.color.background_blue
                )
            }
            Spacer(modifier = modifier.height(20.dp))
            com.kltn.anigan.ui.shared.components.ListButton()
        }

    }
}

@Composable
fun Header(
    navController: NavController,
    modifier: Modifier = Modifier
) {
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
        Image(
            painter = painterResource(id = R.drawable.icon_library),
            contentDescription = "icon_library",
            modifier
                .padding(start = 17.dp, top = 16.dp, end = 12.dp)
                .size(17.dp)
        )

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
            .height(46.dp)
        ,
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

private fun sharePhotoFB(image: Bitmap): SharePhotoContent {
    val photo = SharePhoto.Builder()
        .setBitmap(image)
        .build()
    return SharePhotoContent.Builder()
        .addPhoto(photo)
        .build()
}


@SuppressLint("Recycle")
private fun generateImage(
    capturedImageUri: Uri,
    modelId: Int,
    context: Context,
    setImageFromResponse: (TransformResponse) -> Unit
    ) {
    if (capturedImageUri == Uri.EMPTY) {
        Toast.makeText(context, "Choose an image first!", Toast.LENGTH_LONG).show()
        return
    }

    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
        capturedImageUri, "r", null
    ) ?: return

    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
    val file = File(
        context.cacheDir,
        context.contentResolver.getFileName(capturedImageUri)
    )
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)

    val body = UploadRequestBody(file, "image")
    UploadApi().uploadImage(
        MultipartBody.Part.createFormData(
            "file",
            file.name,
            body
        ),
        modelId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
        "".toRequestBody("text/plain".toMediaTypeOrNull()),
    ).enqueue(object : Callback<TransformResponse> {
        override fun onResponse(
            call: Call<TransformResponse>,
            response: Response<TransformResponse>
        ) {
            response.body()?.let {
                Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                setImageFromResponse(it)
            }
        }

        override fun onFailure(call: Call<TransformResponse>, t: Throwable) {
            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                .show()
        }
    })
}

private fun ContentResolver.getFileName(capturedImageUri: Uri): String {
    var name = ""
    val returnCursor = this.query(capturedImageUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }

    return name
}