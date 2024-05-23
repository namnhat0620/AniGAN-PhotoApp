package com.kltn.anigan.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.kltn.anigan.api.GetMyPlanApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.response.GetMyPlanResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlanUtils {
    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        @SuppressLint("HardwareIds")
        fun getMyPlan(context: Context, viewModel: DocsViewModel) {
            val deviceId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            GetMyPlanApi().getMyPlan(
                if (viewModel.accessToken.value != "") "Bearer ${viewModel.accessToken.value}"
                else "", deviceId
            ).enqueue(object :
                Callback<GetMyPlanResponse> {
                override fun onResponse(
                    call: Call<GetMyPlanResponse>,
                    response: Response<GetMyPlanResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            GlobalScope.launch {
                                viewModel.expiration.value = it.data.expired_day
                                viewModel.numberOfGeneration.intValue = it.data.remain_generation
                                DataStoreManager.saveNoOfGeneration(
                                    context,
                                    it.data.remain_generation.toString()
                                )
                            }
                            Toast.makeText(context, "Successfully", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GetMyPlanResponse>, t: Throwable) {
                    Log.i("Load My Plan Response", "onFailure: ${t.message}")
                }
            })
        }
    }
}