package com.kltn.anigan.domain

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.kltn.anigan.R

class DocsViewModel {

    // Base canvas
    val recompose = mutableIntStateOf(0)
    var saveCanvas = mutableStateOf(false)
    val undoCanvas = mutableStateOf(false)
    var canvasEdit = mutableStateOf(true)
    var uri = mutableStateOf("")
    var bitmap = mutableStateOf<Bitmap?>(null)

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
    var isLoading = mutableStateOf(false)
    var reference = mutableIntStateOf(0)

    // For add hair
    var hairResourceId = mutableIntStateOf(R.drawable.male_3)
    var hairSizeAlpha = mutableFloatStateOf(30F)
}