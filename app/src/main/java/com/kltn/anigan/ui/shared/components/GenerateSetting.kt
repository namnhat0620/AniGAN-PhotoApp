package com.kltn.anigan.ui.shared.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kltn.anigan.routes.Routes

@SuppressLint("Recycle")
@Composable
fun GenerateSetting(
    isEnabled: Boolean,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GradientButton(
            gradientColors = listOf(Color(0xFF00FFF0), Color(0xFF00FF66)),
            cornerRadius = 16.dp,
            nameButton = "Generate Now!",
            roundedCornerShape = RoundedCornerShape(size = 30.dp),
            isEnabled,
            onClick = {
                navController.navigate(Routes.AI_RESULT_SCREEN.route)
            }
        )
    }
}