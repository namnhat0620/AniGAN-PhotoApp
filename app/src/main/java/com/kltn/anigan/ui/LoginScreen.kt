package com.kltn.anigan.ui

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.api.LoginApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.request.LoginRequestBody
import com.kltn.anigan.domain.response.LoginResponse
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.ConditionRow
import com.kltn.anigan.utils.DataStoreManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current

    val password = viewModel.password
    var passwordVisibility by remember { mutableStateOf(false) }
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
    val username = viewModel.username
    val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()

    val isEnabledLogin = usernameError.successful && passwordError.successful
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 12.dp, end = 12.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "icon_close",
            Modifier
                .size(17.dp)
                .fillMaxWidth()
                .align(Alignment.Start)
                .clickable { navController.navigate(Routes.MAIN_SCREEN.route) }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Login", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = username,
            onValueChange = viewModel::changeUsername,
            isError = username.isNotEmpty() && !usernameError.successful,
            label = {
                Text(text = "Username")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if(username.isNotEmpty() && !usernameError.successful) {
            ConditionRow(condition = "Invalid username")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password,
            onValueChange = viewModel::changePassword,
            label = {
                Text(text = "Password")
            },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            isError = password.isNotEmpty() && !passwordError.successful,
            trailingIcon = {
                val image = if (passwordVisibility)
                    R.drawable.baseline_visibility_off_24
                else R.drawable.round_visibility_24

                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Image(painter = painterResource(id = image), "")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if(password.isNotEmpty() && !passwordError.successful) {
            ConditionRow(condition = if (
                password.contains(" ") ||
                password.contains("\t") ||
                password.contains("\n")
            ) "Invalid password"
            else "Please enter at least 6 characters"
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { login(context, viewModel, password, navController) },
            enabled = isEnabledLogin,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Blue,
                disabledContainerColor = Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text(text = "Log in", color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Haven't got an account? ", color = Color.Gray)
            Text(text = "Sign Up", color = Color.Blue,
                modifier = Modifier.clickable { navController.navigate(Routes.SIGN_UP.route) }
            )
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("Recycle")
fun login(
    context: Context,
    viewModel: DocsViewModel,
    password: String,
    navController: NavController
) {
    LoginApi().login(
        LoginRequestBody(username = viewModel.username, password = password)
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
                        DataStoreManager.saveUsername(context, viewModel.username)
                        DataStoreManager.saveRefreshToken(context, it.refresh_token)
                    }
                    navController.navigate(Routes.MAIN_SCREEN.route)
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