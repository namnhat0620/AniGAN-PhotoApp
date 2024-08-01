package com.kltn.anigan.ui.shared.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.kltn.anigan.R
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.ImageClassFromInternet
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.BitmapUtils.Companion.getBitmapFromUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun PhotoLibrary(
    itemList: List<ImageClassFromInternet>,
    viewModel: DocsViewModel,
    navController: NavController,
    loadMore: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var currentItemList by remember { mutableStateOf(itemList) }

    LaunchedEffect(itemList) {
        currentItemList = itemList
        listState.scrollToItem(0)
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .padding(vertical = 15.dp)
    ) {
        items(currentItemList, key = { it.image_id }) { item ->
            if (item.url.isNotEmpty()) {
                var bitmap by remember { mutableStateOf<Bitmap?>(null) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(item.url) {
                    // Launch a coroutine in the composition scope
                    bitmap = withContext(Dispatchers.IO) {
                        getBitmapFromUrl(item.url, context, viewModel.accessToken.value) // Call suspending function
                    }
                    isLoading = false
                }

                if (!isLoading) {
                    GlideImage(
                        model = bitmap,
                        failure = placeholder(R.drawable.default_image),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(100.dp)
                            .clickable {
                                fetchBitmapAndNavigate(item.url, context, viewModel, navController)
                            }
                    )
                }
            } else {
                Box(modifier = Modifier.size(100.dp)) {
                    CircularProgressIndicator()
                }
            }
        }

        item {
            if (viewModel.isLoadingUserImages.value || viewModel.isLoadingAniganImages.value) {
                CircularProgressIndicator(modifier = Modifier.padding(12.dp))
            } else {
                Spacer(modifier = Modifier.size(100.dp))
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
            .distinctUntilChanged()
            .collect { visibleItem ->
                visibleItem?.let {
                    if (visibleItem.index == itemList.size - 1) {
                        loadMore()
                    }
                }
            }
    }
}

private fun fetchBitmapAndNavigate(
    url: String,
    context: Context,
    viewModel: DocsViewModel,
    navController: NavController
) {
    viewModel.viewModelScope.launch(Dispatchers.IO) {
        try {
            val bitmap = getBitmapFromUrl(
                context = context,
                urlString = url,
                accessToken = viewModel.accessToken.value
            )
            withContext(Dispatchers.Main) {
                viewModel.bitmap = bitmap
                viewModel.url.value = url
                navController.navigate(Routes.EDIT_SCREEN.route)
            }
        } catch (error: IOException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
