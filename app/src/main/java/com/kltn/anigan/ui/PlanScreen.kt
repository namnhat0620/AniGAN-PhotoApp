package com.kltn.anigan.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.kltn.anigan.R
import com.kltn.anigan.api.GetPlanApi
import com.kltn.anigan.api.RegisterPlanApi
import com.kltn.anigan.domain.BillingViewModel
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.billing.Product
import com.kltn.anigan.domain.request.RegisterPlanBody
import com.kltn.anigan.domain.response.LoadPlanResponse
import com.kltn.anigan.domain.response.RegisterPlanResponse
import com.kltn.anigan.routes.Routes
import com.kltn.anigan.ui.shared.components.GradientButton
import com.kltn.anigan.utils.DataStoreManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executors

@Composable
fun PlanScreen(
    navController: NavController,
    viewModel: DocsViewModel,
    billingViewModel: BillingViewModel
) {
    var listPlanIds by remember { mutableStateOf(intArrayOf()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        getAllPlan(context, viewModel) {
            listPlanIds = it
        }
        getPrice(context as Activity, billingViewModel)
    }

    Header(navController, viewModel, billingViewModel)
}


@Composable
private fun Header(
    navController: NavController,
    viewModel: DocsViewModel,
    billingViewModel: BillingViewModel
) {
    Column(
        Modifier.background(Color.Black)
    ) {
        TabBar(navController, viewModel, billingViewModel)
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabBar(
    navController: NavController,
    viewModel: DocsViewModel,
    billingViewModel: BillingViewModel
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val pages = listOf("Pro", "Pro+")

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        CustomIndicator(tabPositions, pagerState)
    }

    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ScrollableTabRow(
                modifier = Modifier.height(50.dp),
                selectedTabIndex = pagerState.currentPage,
                indicator = indicator,
                backgroundColor = Color(0xFF202020)
            ) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        modifier = Modifier.zIndex(2f),
                        text = { Text(text = title, color = Color.White) },
                        selected = pagerState.currentPage == index,
                        onClick = {}
                    )
                }
            }
        }

    }

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        count = pages.size,
        state = pagerState
    ) { page ->
        when (page) {
            0 -> Page1(
                context, navController, viewModel, billingViewModel
            )

            1 -> Page2(
                context, navController, viewModel, billingViewModel
            )
        }
    }


}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CustomIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
    val transition = updateTransition(pagerState.currentPage, label = "")
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        },
        label = ""
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        },
        label = ""
    ) {
        tabPositions[it].right
    }

    Box(
        Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart)
            .padding(2.dp)
            .fillMaxSize()
            .background(color = Color(0xFFF3A100), RoundedCornerShape(50))
            .zIndex(1f)
    )
}

@Composable
private fun Page1(
    context: Context,
    navController: NavController,
    viewModel: DocsViewModel,
    billingViewModel: BillingViewModel
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.pro), "",
            Modifier
                .fillMaxWidth()
        )
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(Modifier.height(12.dp))
            GradientButton(
                gradientColors = listOf(Color(0xFFF8FC34), Color(0xFFF3A100)),
                cornerRadius = 16.dp,
                nameButton = "$0,99/month",
                roundedCornerShape = RoundedCornerShape(size = 30.dp),
                true,
                onClick = {
                    registerPlan(context, viewModel, billingViewModel, 3)
                    navController.navigate(Routes.PROFILE.route)
                }
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { navController.navigate(Routes.MAIN_SCREEN.route) },
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Cancel", color = Color.White)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable
private fun Page2(
    context: Context,
    navController: NavController,
    viewModel: DocsViewModel,
    billingViewModel: BillingViewModel
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.proplus), "",
            Modifier
                .fillMaxWidth()
        )
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(Modifier.height(12.dp))
            GradientButton(
                gradientColors = listOf(Color(0xFFF8FC34), Color(0xFFF3A100)),
                cornerRadius = 16.dp,
                nameButton = "$9,99/month",
                roundedCornerShape = RoundedCornerShape(size = 30.dp),
                true,
                onClick = {
                    registerPlan(context, viewModel, billingViewModel, 4)
                    navController.navigate(Routes.PROFILE.route)
                }
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { navController.navigate(Routes.MAIN_SCREEN.route) },
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = "Cancel", color = Color.White)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

private fun getAllPlan(context: Context, viewModel: DocsViewModel, onResponse: (IntArray) -> Unit) {
    val listPlanIds: IntArray = intArrayOf()

    try {
        GetPlanApi().getAllPlan(
            if (viewModel.accessToken.value != "") "Bearer ${viewModel.accessToken.value}"
            else ""
        ).enqueue(object :
            Callback<LoadPlanResponse> {
            override fun onResponse(
                call: Call<LoadPlanResponse>,
                response: Response<LoadPlanResponse>
            ) {
                response.body()?.let { body ->
                    body.data.list.forEach { plan ->
                        listPlanIds.plus(plan.plan_id)
                    }
                    onResponse(listPlanIds)
                }
            }

            override fun onFailure(call: Call<LoadPlanResponse>, t: Throwable) {
                Log.i("Logout failed", "onFailure: ${t.message}")
            }
        })
    } catch (e: IOException) {
        Toast.makeText(context, "Failed by: ${e.message}", Toast.LENGTH_LONG).show()
    }

}

private fun registerPlan(context: Context, viewModel: DocsViewModel, billingViewModel: BillingViewModel, planId: Int) {
    billingViewModel.billingClient!!.startConnection(object :BillingClientStateListener{
        override fun onBillingServiceDisconnected() {
            TODO("Not yet implemented")
        }

        override fun onBillingSetupFinished(p0: BillingResult) {
            viewModel.planIdToRegister.value = planId
            val productList = listOf(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(if(planId == 3) "13" else "14")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)

            billingViewModel.billingClient!!.queryProductDetailsAsync(params.build()) {
                billingResult, productDetailsList ->

                for (productDetails in productDetailsList) {
                    val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
                    val productDetailsParamsList =
                        listOf(
                            offerToken?.let {
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(it)
                                    .build()
                            }
                        )
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()
                    val billingResult = billingViewModel.billingClient!!.launchBillingFlow(context as Activity, billingFlowParams)
                }
            }
        }

    })

//    if (planId < 0) return
//    RegisterPlanApi().registerPlan(
//        "Bearer ${viewModel.accessToken.value}",
//        RegisterPlanBody(planId)
//    ).enqueue(object :
//        Callback<RegisterPlanResponse> {
//        override fun onResponse(
//            call: Call<RegisterPlanResponse>,
//            response: Response<RegisterPlanResponse>
//        ) {
//            response.body()?.let {
//                viewModel.numberOfGeneration.intValue = it.data.number_of_generation
//                GlobalScope.launch(Dispatchers.Main) {
//                    DataStoreManager.saveNoOfGeneration(
//                        context,
//                        it.data.number_of_generation.toString()
//                    )
//                }
//            }
//        }
//
//        override fun onFailure(call: Call<RegisterPlanResponse>, t: Throwable) {
//            Log.i("Logout failed", "onFailure: ${t.message}")
//        }
//    })
}

private fun getPrice(activity: Activity, billingViewModel: BillingViewModel) {
    billingViewModel.billingClient!!.startConnection(object :BillingClientStateListener{
        override fun onBillingServiceDisconnected() {}

        override fun onBillingSetupFinished(p0: BillingResult) {
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute{
                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("13")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("14")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
                val params = QueryProductDetailsParams.newBuilder().setProductList(productList)
                billingViewModel.billingClient!!.queryProductDetailsAsync(params.build()) {billingResult, productDetailsList ->
                    for (productDetails in productDetailsList) {
                        val response = productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice ?: ""
                        val sku = productDetails.name
                        val ds = productDetails.description
                        val des = "$sku : $ds : price: $response"

                        billingViewModel.productList.add(Product(response, des, sku))
                    }
                }
            }
            activity.runOnUiThread{
                try {
                    Thread.sleep(1000)

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    })
}