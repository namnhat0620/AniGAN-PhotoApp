package com.kltn.anigan.ui.shared.layouts.footers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.ui.shared.components.ListButton


@Composable
fun EditFooter(
    navController: NavController,
    viewModel: DocsViewModel,
    isLoading: Boolean
) {
    Row(
        Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.Bottom
    ) {
        ListButton(navController = navController, viewModel, isLoading)
    }

}