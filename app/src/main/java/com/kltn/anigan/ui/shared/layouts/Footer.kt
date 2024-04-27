package com.kltn.anigan.ui.shared.layouts

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kltn.anigan.R


@Composable
fun Footer(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row (
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ){
        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = "icon_change_image",
            modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(17.dp)
                .clickable {
                           navController.popBackStack()
                },
        )
        Row {
            Image(
                painter = painterResource(id = R.drawable._icon__check_),
                contentDescription = "icon_change_image",
                modifier
                    .padding(start = 12.dp, top = 16.dp, end = 12.dp)
                    .size(17.dp)
                    .clickable { navController.popBackStack()}
            )

        }
    }
}
