package com.kltn.anigan.api

import com.kltn.anigan.api.BaseURL.BaseURL.BASE_URL
import com.kltn.anigan.domain.request.TransformRequest
import com.kltn.anigan.domain.response.TransformResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface TransformApi {
    @POST("image/transform")
    fun transformImage(
        @Body body: TransformRequest
    ): Call<TransformResponse>

    companion object {
        operator fun invoke(): TransformApi{
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
                .create(TransformApi::class.java)
        }
    }
}