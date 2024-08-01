@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.kltn.anigan.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kltn.anigan.R
import com.kltn.anigan.api.LoadImageApi
import com.kltn.anigan.domain.enums.ImageType
import com.kltn.anigan.domain.enums.ResolutionOption
import com.kltn.anigan.domain.response.LoadImageResponse
import com.kltn.anigan.domain.validators.confirmPassword.ConfirmPasswordValidationState
import com.kltn.anigan.domain.validators.confirmPassword.ValidateConfirmPassword
import com.kltn.anigan.domain.validators.email.EmailValidationState
import com.kltn.anigan.domain.validators.email.ValidateEmail
import com.kltn.anigan.domain.validators.name.NameValidationState
import com.kltn.anigan.domain.validators.name.ValidateName
import com.kltn.anigan.domain.validators.password.PasswordValidationState
import com.kltn.anigan.domain.validators.password.ValidatePassword
import com.kltn.anigan.domain.validators.username.UsernameValidationState
import com.kltn.anigan.domain.validators.username.ValidateUsername
import com.kltn.anigan.utils.DataStoreManager
import com.kltn.anigan.utils.HardwareUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class DocsViewModel(
    private val validateUsername: ValidateUsername = ValidateUsername(),
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validateName: ValidateName = ValidateName(),
    private val validatePassword: ValidatePassword = ValidatePassword(),
    private val validateConfirmPassword: ValidateConfirmPassword = ValidateConfirmPassword()
) : ViewModel() {
    // authorization
    val accessToken = mutableStateOf("")
    val refreshToken = mutableStateOf("")
    var hasPlan = mutableStateOf(false)
    var expiration = mutableStateOf("")
    var numberOfGeneration = mutableIntStateOf(0)

    // Validators username
    var username by mutableStateOf("")
        private set

    val usernameError = snapshotFlow { username }
        .mapLatest { validateUsername.execute(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UsernameValidationState()
        )

    fun changeUsername(value: String) {
        username = value
    }

    // Validators password
    var password by mutableStateOf("")
        private set

    val passwordError = snapshotFlow { password }
        .mapLatest { validatePassword.excute(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PasswordValidationState()
        )

    fun changePassword(value: String) {
        password = value
    }

    // Validators confirm password
    var confirmPassword by mutableStateOf("")
        private set

    val confirmPasswordError = snapshotFlow { confirmPassword }
        .mapLatest { validateConfirmPassword.excute(it, password) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConfirmPasswordValidationState()
        )

    fun changeConfirmPassword(value: String) {
        confirmPassword = value
    }

    // Validators email
    var email by mutableStateOf("")
        private set

    val emailError = snapshotFlow { email }
        .mapLatest { validateEmail.excute(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmailValidationState()
        )

    fun changeEmail(value: String) {
        email = value
    }

    // Validators firstName
    var firstName by mutableStateOf("")
        private set

    val firstNameError = snapshotFlow { firstName }
        .mapLatest { validateName.excute(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NameValidationState()
        )

    fun changeFirstName(value: String) {
        firstName = value
    }

    // Validators lastName
    var lastName by mutableStateOf("")
        private set

    val lastNameError = snapshotFlow { lastName }
        .mapLatest { validateName.excute(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NameValidationState()
        )

    fun changeLastName(value: String) {
        lastName = value
    }

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
    var angle = mutableFloatStateOf(0f)

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
    var resolutionOption = mutableStateOf(ResolutionOption._512x512.value)

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

    @OptIn(DelicateCoroutinesApi::class)
    fun resetAll(context: Context, viewModel: DocsViewModel) {
        viewModel.accessToken.value = ""
        viewModel.changeUsername("")
        viewModel.changeFirstName("")
        viewModel.changeLastName("")
        viewModel.hasPlan.value = false
        viewModel.numberOfGeneration.intValue = 0
        viewModel.expiration.value = ""

        GlobalScope.launch {
            DataStoreManager.clearUsername(context)
            DataStoreManager.clearRefreshToken(context)
            DataStoreManager.clearNoOfGeneration(context)
        }
    }

    fun loadMoreUserImages(deviceId: String, viewModel: DocsViewModel) {
        viewModelScope.launch {
            if (!isLoadingUserImages.value) {
                isLoadingUserImages.value = true
                getImage(deviceId = deviceId, viewModel = viewModel, type = ImageType.USER_IMAGE.type, page = userImagesPage.intValue) {
                    userImages.addAll(it)
                }
                userImagesPage.intValue += 1
                isLoadingUserImages.value = false
            }
        }
    }

    fun loadMoreAniganImages(deviceId: String, viewModel: DocsViewModel) {
        viewModelScope.launch {
            if (!isLoadingAniganImages.value) {
                isLoadingAniganImages.value = true
                getImage(deviceId = deviceId, viewModel = viewModel, type = ImageType.ANIGAN_IMAGE.type, page = aniganImagesPage.intValue) {
                    aniganImages.addAll(it)
                }
                aniganImagesPage.intValue += 1
                isLoadingAniganImages.value = false
            }
        }
    }
}

private fun getImage(
    viewModel: DocsViewModel,
    deviceId: String,
    type: Int,
    page: Int,
    onImageListLoaded: (List<ImageClassFromInternet>) -> Unit
) {
    LoadImageApi().getRefImage(
        if (viewModel.accessToken.value != "") "Bearer ${viewModel.accessToken.value}"
        else "",
        deviceId, type, page).enqueue(object :
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