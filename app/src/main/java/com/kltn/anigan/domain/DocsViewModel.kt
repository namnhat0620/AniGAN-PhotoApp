package com.kltn.anigan.domain

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

class DocsViewModel {

    // Base canvas
    val recompose = mutableIntStateOf(0)
    var saveCanvas = mutableStateOf(false)
    val undoCanvas = mutableStateOf(false)
    var canvasEdit = mutableStateOf(true)

    // For add brush
    var brushSize = mutableFloatStateOf(10f)
    var opacity = mutableFloatStateOf(100f)
    var color = mutableStateOf(Color.Red)

    // For add text
    var text = mutableStateOf("Input your text")
    var x = mutableFloatStateOf(0f)
    var y = mutableFloatStateOf(0f)
    var textSize = mutableFloatStateOf(50f)
}