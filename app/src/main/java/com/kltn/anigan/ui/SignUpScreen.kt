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
import com.kltn.anigan.api.SignUpApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.request.SignUpRequestBody
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.ConditionRow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    //Validate
    val username = viewModel.username
    val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()

    val email = viewModel.email
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()

    val password = viewModel.password
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()

    val confirmPassword = viewModel.confirmPassword
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsStateWithLifecycle()

    val firstName = viewModel.firstName
    val firstNameError by viewModel.firstNameError.collectAsStateWithLifecycle()

    val lastName = viewModel.lastName
    val lastNameError by viewModel.lastNameError.collectAsStateWithLifecycle()

    val isEnabledSignUp =
                usernameError.successful &&
                emailError.successful &&
                passwordError.successful &&
                confirmPasswordError.successful &&
                firstNameError.successful &&
                lastNameError.successful

//                username.isNotBlank() &&
//                password.isNotBlank() &&
//                email.isNotEmpty() &&
//                firstName.isNotEmpty() &&
//                lastName.isNotEmpty()
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
            value = email,
            onValueChange = viewModel::changeEmail,
            isError = email.isNotEmpty() && !emailError.successful,
            label = {
                Text(text = "Email")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if(email.isNotEmpty() && !emailError.successful) {
            ConditionRow(condition = "Invalid email address")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = viewModel::changeFirstName,
            isError = firstName.isNotEmpty() && !firstNameError.successful,
            label = {
                Text(text = "First name")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if(firstName.isNotEmpty() && !firstNameError.successful) {
            ConditionRow(condition = "Invalid firstname")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = viewModel::changeLastName,
            isError = lastName.isNotEmpty() && !lastNameError.successful,
            label = {
                Text(text = "Last name")
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if(lastName.isNotEmpty() && !lastNameError.successful) {
            ConditionRow(condition = "Invalid lastname")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password,
            onValueChange = viewModel::changePassword,
            isError = password.isNotEmpty() && !passwordError.successful,
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

        // Confirm password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = viewModel::changeConfirmPassword,
            isError = confirmPassword.isNotEmpty() && !confirmPasswordError.successful,
            label = {
                Text(text = "Confirm password")
            },
            visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisibility)
                    R.drawable.baseline_visibility_off_24
                else R.drawable.round_visibility_24

                IconButton(onClick = {
                    confirmPasswordVisibility = !confirmPasswordVisibility
                }) {
                    Image(painter = painterResource(id = image), "")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if(confirmPassword.isNotEmpty() && !confirmPasswordError.successful) {
            ConditionRow(condition = "Incorrect password")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = {
                signUp(
                    context,
                    viewModel,
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
    navController: NavController
) {
    SignUpApi().signup(
        SignUpRequestBody(
            username = viewModel.username,
            password = viewModel.password,
            email = viewModel.email,
            firstName = viewModel.firstName,
            lastName = viewModel.lastName,
            enabled = true
        )
    ).enqueue(object : Callback<Void> {
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetAll(context, viewModel)
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