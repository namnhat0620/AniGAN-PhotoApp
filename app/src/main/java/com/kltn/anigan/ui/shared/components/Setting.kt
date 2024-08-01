package com.kltn.anigan.ui.shared.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.enums.ResolutionOption
import com.kltn.anigan.utils.BitmapUtils.Companion.dpFromPx

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResolutionSetting(viewModel: DocsViewModel) {
    val context = LocalContext.current
    Column {
        var offset = Offset.Zero
        var dropDownExpanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { dropDownExpanded = true },
                Modifier.pointerInteropFilter {
                        offset = Offset(it.x, it.y)
                        false
                    }
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Localized description", tint = Color.White
                )
            }
            Text(text = ResolutionOption.toString(viewModel.resolutionOption.value), color = Color.White)
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            DropdownMenu(
                expanded = dropDownExpanded,
                offset = DpOffset(dpFromPx(context, offset.x).dp, dpFromPx(context, offset.y + 10).dp),
                onDismissRequest = { dropDownExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Small") },
                    onClick = {
                        viewModel.resolutionOption.value = ResolutionOption._256x256.value
                        dropDownExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Medium") },
                    onClick = {
                        viewModel.resolutionOption.value = ResolutionOption._512x512.value
                        dropDownExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Large") },
                    onClick = {
                        viewModel.resolutionOption.value = ResolutionOption._1024x1024.value
                        dropDownExpanded = false
                    }
                )
            }
        }
    }
}

