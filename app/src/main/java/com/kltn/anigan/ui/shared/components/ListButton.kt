package com.kltn.anigan.ui.shared.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.utils.BitmapUtils
import com.kltn.anigan.utils.UriUtils
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
fun ListButton(navController: NavController, viewModel: DocsViewModel, isLoading: Boolean) {
    val context = LocalContext.current
    val cropLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val newUri = UCrop.getOutput(data!!)
                if (newUri != null) {
                    // Get des uri
                    viewModel.uri.value = newUri.toString()
                    viewModel.bitmap = BitmapUtils.getBitmapFromUri(newUri, context)
                    navController.navigate(Routes.EDIT_SCREEN.route)
                }
            }
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FuncButton2(
                R.drawable.baseline_auto_awesome_24, "AI Tools", isLoading
            ) {
                navController.navigate(Routes.AI_TOOLS.route)
            }
            FuncButton2(
                R.drawable._crop, "Crop", isLoading
            ) {
                cropActivity(context, viewModel, cropLauncher)
            }
            FuncButton2(
                R.drawable.baseline_brush_24, "Brush", isLoading
            ) {
                navController.navigate(Routes.HAIR_SCREEN.route)
            }
            FuncButton2(
                R.drawable.baseline_text_fields_24, "Text", isLoading
            ) {
                navController.navigate(Routes.ADD_TEXT.route)
            }
            FuncButton2(
                R.drawable.baseline_auto_fix_high_24, "Filters", isLoading
            ) {
                navController.navigate(Routes.FILTER_TOOL.route)
            }
        }

    }
}

@Composable
private fun FuncButton2(imageId: Int, text: String, isLoading: Boolean, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                enabled = !isLoading
            ) { onClick() }
            .alpha(if (isLoading) 0.4f else 1f)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "edit_btn",
            Modifier.size(23.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = text,
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 15.sp
        )
    }
}

private fun cropActivity(
    context: Context,
    viewModel: DocsViewModel,
    cropLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val destinationFileName = "image_${System.currentTimeMillis()}.jpg"
    val destinationUri = Uri.fromFile(File(context.cacheDir, destinationFileName))

    // Get src uri
    if (viewModel.bitmap != null) {
        UriUtils.bitmapToUri(context, viewModel.bitmap!!)?.let {
            val uri = it
            viewModel.uri.value = it.toString()

            // Crop
            val uCrop = UCrop.of(uri, destinationUri)
            val uCropIntent = uCrop.getIntent(context)
            cropLauncher.launch(uCropIntent)
        }
    }
}