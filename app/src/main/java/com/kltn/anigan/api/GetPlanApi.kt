package com.kltn.anigan.api

import com.kltn.anigan.api.BaseURL.BaseURL.BASE_URL
import com.kltn.anigan.domain.response.LoadPlanResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit

interface GetPlanApi {
    @GET("/plan")
    @Headers("Content-Type: application/json")
    fun getAllPlan(): Call<LoadPlanResponse>

    companion object {
        operator fun invoke(): GetPlanApi {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Set log level to BODY to log request and response data

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor) // Add the logging interceptor
                .connectTimeout(1, TimeUnit.HOURS) // Example: Set connect timeout to 10 seconds
                .readTimeout(1, TimeUnit.HOURS) // Example: Set read timeout to 10 seconds
                .writeTimeout(1, TimeUnit.HOURS) // Example: Set write timeout to 10 seconds
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetPlanApi::class.java)
        }
    }
}