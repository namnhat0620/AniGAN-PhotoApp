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
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.api.SignUpApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.request.SignUpRequestBody
import com.kltn.anigan.routes.Routes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current
    var passwordVisibility by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val isEnabledSignUp =
        viewModel.username.value.isNotEmpty() &&
                password.isNotEmpty() &&
                email.isNotEmpty() &&
                firstName.isNotEmpty() &&
                lastName.isNotEmpty()
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

        Text(text = "Sign up", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = viewModel.username.value,
            onValueChange = {
                viewModel.username.value = it
            },
            label = {
                Text(text = "Username")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
            },
            label = {
                Text(text = "First name")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
            },
            label = {
                Text(text = "Last name")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
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
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = {
                signUp(
                    context,
                    viewModel,
                    password,
                    firstName,
                    lastName,
                    email,
                    navController
                )
            },
            enabled = isEnabledSignUp,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Blue,
                disabledContainerColor = Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign up", color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", color = Color.Gray)
            Text(text = "Log in", color = Color.Blue,
                modifier = Modifier.clickable { navController.navigate(Routes.LOGIN.route) }
            )
        }
    }
}

@SuppressLint("Recycle")
private fun signUp(
    context: Context,
    viewModel: DocsViewModel,
    password: String,
    firstName: String,
    lastName: String,
    email: String,
    navController: NavController
) {
    SignUpApi().signup(
        SignUpRequestBody(
            username = viewModel.username.value,
            password = password,
            email = email,
            firstName = firstName,
            lastName = lastName,
            enabled = true
        )
    ).enqueue(object : Callback<Void> {
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.LOGIN.route)
            } else {
                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "Fail by ${t.message!!}!", Toast.LENGTH_LONG)
                .show()
        }
    })
}