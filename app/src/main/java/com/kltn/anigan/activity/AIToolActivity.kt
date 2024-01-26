package com.kltn.anigan.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kltn.anigan.R
import com.kltn.anigan.ui.AIToolScreen
import com.kltn.anigan.ui.MainScreen
import com.kltn.anigan.ui.theme.AniGANTheme

class AIToolActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aitool)
        setContent {
            AniGANTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AIToolScreen()
                }
            }
        }
    }
}