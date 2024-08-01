package com.kltn.anigan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kltn.anigan.R
import com.kltn.anigan.api.LogoutApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.utils.PlanUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("HardwareIds")
@Composable
fun ProfileScreen(navController: NavController, viewModel: DocsViewModel) {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Header()
        UserInfo(navController, viewModel)
        Banner(R.drawable.subscription, navController)
        Func(viewModel)
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            OutlinedButton(
                onClick = {
                    logout(context, viewModel, navController)
                },
                Modifier.fillMaxWidth()
            ) {
                Text(text = "Log out", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.MAIN_SCREEN.route)
                },
                Modifier.fillMaxWidth()
            ) {
                Text(text = "Back", color = Color.White)
            }
        }
    }

}

@Composable
private fun Header() {
    Row(
        Modifier
            .height(50.dp)
            .fillMaxWidth()
            .padding(6.dp)
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Profile", fontSize = 25.sp, color = Color.White)
        //Icon notification
//        OutlinedButton(
//            onClick = {
//                navController?.navigate(Routes.LOGIN.route)
//            }
//        ) {
//            if(viewModel?.isLogin?.value == true) {
//                Image(
//                    painter = painterResource(id = R.drawable.round_account_circle_24),
//                    contentDescription = ""
//                )
//                Spacer(Modifier.width(1.dp))
//                viewModel.username.value.let { Text(text = it, color = Color.White) }
//            }
//            else {
//                Text(text = "Login", color = Color.White)
//            }
//        }
    }
}

@Composable
private fun UserInfo(navController: NavController?, viewModel: DocsViewModel) {
    Row(
        Modifier
            .height(60.dp)
            .fillMaxWidth()
            .padding(6.dp)
            .background(colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.round_account_circle_24),
                modifier = Modifier.size(80.dp),
                contentDescription = ""
            )
            Column {
                Text(text = viewModel.userName.value, color = Color.White, fontSize = 20.sp)
                Text(
                    text = viewModel.expiration.value,
                    color = Color.Yellow,
                    fontSize = 10.sp
                )
            }
        }

        //Icon notification
        OutlinedButton(
            onClick = {
                navController?.navigate(Routes.LOGIN.route)
            },
            modifier = Modifier.padding(0.dp)
        ) {
            if (viewModel.userName.value.isNotEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_energy_savings_leaf_24),
                    contentDescription = ""
                )
                Spacer(Modifier.width(1.dp))
                viewModel.numberOfGeneration.intValue.let {
                    Text(
                        text = "$it",
                        color = Color.White
                    )
                }
            } else {
                Text(text = "Login", color = Color.White)
            }
        }
    }
}

@SuppressLint("HardwareIds")
@Composable
private fun Func(viewModel: DocsViewModel) {
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {}
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            drawRoundRect(
                color = Color(0xFF202020),
                size = size,
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = Fill
            )
        }
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable { cameraLauncher.launch(Uri.EMPTY) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.outline_camera_alt_24), "",
                        Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text("Camera", color = Color.White)
                }
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                    "", Modifier.size(18.dp)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 0.dp, end = 15.dp, bottom = 15.dp)
                    .clickable {
                        PlanUtils.getMyPlan(context, viewModel)
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.outline_autorenew_24), "",
                        Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text("Restore Purchases", color = Color.White)
                }
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24), "",
                    Modifier.size(18.dp)
                )
            }
        }

    }
}


@Composable
private fun Banner(id: Int, navController: NavController?) {
    Image(
        painter = painterResource(id = id),
        contentDescription = "banner",
        Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp, horizontal = 6.dp)
            .clickable { navController?.navigate(Routes.PLAN.route) }
    )
}

private fun logout(context: Context, viewModel: DocsViewModel, navController: NavController) {
    LogoutApi().logout("Bearer ${viewModel.refreshToken.value}").enqueue(object :
        Callback<Void> {
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            viewModel.resetAll(context, viewModel)
            viewModel.userName.value = ""
            navController.navigate(Routes.MAIN_SCREEN.route)
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.i("Logout failed", "onFailure: ${t.message}")
        }
    })
}