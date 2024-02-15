package com.kltn.anigan.ui.shared.components

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kltn.anigan.R
import com.kltn.anigan.api.UploadApi
import com.kltn.anigan.domain.UploadRequestBody
import com.kltn.anigan.domain.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

const val MIN_VALUE = 1
const val MAX_VALUE = 20

@Composable
private fun NumOfGeneration(
    numOfGenerations: Int,
    onNumOfGenerationsChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Number of Generated", color = Color.White)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_remove_empty),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        if (numOfGenerations >= MIN_VALUE) {
                            onNumOfGenerationsChanged(numOfGenerations - 1)
                        }
                    }
                    .size(16.dp)
            )
            Text(
                text = numOfGenerations.toString(),
                color = Color.White,
                modifier = Modifier.padding(10.dp, 0.dp))
            Image(
                painter = painterResource(id = R.drawable.icon_add_circled_outline),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        if (numOfGenerations < MAX_VALUE) {
                            onNumOfGenerationsChanged(numOfGenerations + 1)
                        }
                    }
                    .size(16.dp)
            )
        }
    }
}

@SuppressLint("Recycle")
@Composable
fun GenerateSetting(
    capturedImageUri: Uri,
    referenceImageUrl: String?,
    setCapturedImageUri: (Uri) -> Unit,
    ) {
    var numOfGenerations by remember { mutableIntStateOf(2)}
    val context = LocalContext.current
    var isLoading by remember {mutableStateOf(false)}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumOfGeneration(
            numOfGenerations,
            onNumOfGenerationsChanged = { newValue ->
                numOfGenerations = newValue
            }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
        if(!isLoading) {
            GradientButton(
                gradientColors = listOf(Color(0xFF00FFF0), Color(0xFF00FF66)),
                cornerRadius = 16.dp,
                nameButton = "Generate Now!",
                roundedCornerShape = RoundedCornerShape(size = 30.dp),
                onClick = {
                    isLoading = true

                    if (capturedImageUri == Uri.EMPTY) {
                        Toast.makeText(context, "Choose an image first!", Toast.LENGTH_LONG).show()
                        return@GradientButton
                    }

                    if (referenceImageUrl == null) {
                        Toast.makeText(context, "Choose an reference image first!", Toast.LENGTH_LONG).show()
                        return@GradientButton
                    }

                    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                        capturedImageUri, "r", null
                    ) ?: return@GradientButton

                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val file =
                        File(context.cacheDir, context.contentResolver.getFileName(capturedImageUri))
                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)

                    val body = UploadRequestBody(file, "image")
                    UploadApi().uploadImage(
                        MultipartBody.Part.createFormData(
                            "file",
                            file.name,
                            body
                        ),
                        referenceImageUrl.toRequestBody("text/plain".toMediaTypeOrNull()),
                    ).enqueue(object : Callback<UploadResponse> {
                        override fun onResponse(
                            call: Call<UploadResponse>,
                            response: Response<UploadResponse>
                        ) {
                            response.body()?.let {
                                Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                                isLoading = false
                                setCapturedImageUri(Uri.parse(it.url))
                            }
                        }

                        override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                            isLoading = false
                            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            )
            Text(
                text = "Every creation consumes $numOfGenerations credits.",
                color = Color.Gray
            )
        }
        else {
            CircularProgressIndicator()
            Text(
                text = "Waiting for less than 1 minute.",
                color = Color.Gray
            )
        }
    }
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