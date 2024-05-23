package com.kltn.anigan.domain

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kltn.anigan.R
import com.kltn.anigan.api.LoadImageApi
import com.kltn.anigan.domain.enums.ImageType
import com.kltn.anigan.domain.response.LoadImageResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocsViewModel : ViewModel() {
    // authorization
    val accessToken = mutableStateOf("")
    val refreshToken = mutableStateOf("")
    var username = mutableStateOf("")
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var hasPlan = mutableStateOf(false)
    var expiration = mutableStateOf("")
    var numberOfGeneration = mutableIntStateOf(0)

    // Base canvas
    val recompose = mutableIntStateOf(0)
    var saveCanvas = mutableStateOf(false)
    val undoCanvas = mutableStateOf(false)
    var canvasEdit = mutableStateOf(true)
    var uri = mutableStateOf("")
    private var _bitmap by mutableStateOf<Bitmap?>(null)
    var bitmap: Bitmap?
        get() = _bitmap
        set(value) {
            _bitmap = value
            url.value = ""  // Set url to empty string when bitmap changes
        }

    // For add brush
    var brushSize = mutableFloatStateOf(10f)
    var opacity = mutableFloatStateOf(100f)
    var color = mutableStateOf(Color.Red)

    // For add text
    var text = mutableStateOf("Input your text")
    var x = mutableFloatStateOf(0f)
    var y = mutableFloatStateOf(0f)
    var textSize = mutableFloatStateOf(50f)

    // For add filter
    var colorMatrix = mutableStateOf(ColorMatrix())

    // For AI tools
    var url = mutableStateOf("")
    var resultUrl = mutableStateOf("")
    var reference = mutableIntStateOf(0)

    // For add hair
    var hairResourceId = mutableIntStateOf(R.drawable.male_3)
    var hairSizeAlpha = mutableFloatStateOf(300F)

    //For lazy load
    var userImages = mutableStateListOf<ImageClassFromInternet>()
    var aniganImages = mutableStateListOf<ImageClassFromInternet>()
    private var userImagesPage = mutableIntStateOf(1)
    private var aniganImagesPage = mutableIntStateOf(1)
    var isLoadingUserImages = mutableStateOf(false)
    var isLoadingAniganImages = mutableStateOf(false)

    fun loadMoreUserImages() {
        viewModelScope.launch {
            if (!isLoadingUserImages.value) {
                isLoadingUserImages.value = true
                getImage(type = ImageType.USER_IMAGE.type, page = userImagesPage.intValue) {
                    userImages.addAll(it)
                }
                userImagesPage.intValue += 1
                isLoadingUserImages.value = false
            }
        }
    }

    fun loadMoreAniganImages() {
        viewModelScope.launch {
            if (!isLoadingAniganImages.value) {
                isLoadingAniganImages.value = true
                getImage(type = ImageType.ANIGAN_IMAGE.type, page = aniganImagesPage.intValue) {
                    aniganImages.addAll(it)
                }
                aniganImagesPage.intValue += 1
                isLoadingAniganImages.value = false
            }
        }
    }
}

private fun getImage(
    type: Int,
    page: Int,
    onImageListLoaded: (List<ImageClassFromInternet>) -> Unit
) {
    LoadImageApi().getRefImage(type, page).enqueue(object :
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