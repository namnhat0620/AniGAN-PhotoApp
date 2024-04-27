package com.kltn.anigan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kltn.anigan.R

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 12.dp, end=12.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        Image(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "icon_close",
            modifier
                .size(17.dp)
                .fillMaxWidth()
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Login", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = {
                Text(text = "Email address")
            },
            modifier = modifier
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = {
                Text(text = "Password")
            },
            modifier = modifier
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}