package com.kltn.anigan.api

import com.kltn.anigan.api.BaseURL.BaseURL.BASE_URL
import com.kltn.anigan.domain.request.LoginRequestBody
import com.kltn.anigan.domain.response.LoginResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface LoginApi {
    @POST("/keycloak/login")
    @Headers("Content-Type: application/json")
    fun login(@Body loginRequest: LoginRequestBody): Call<LoginResponse>

    companion object {
        operator fun invoke(): LoginApi {
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
                .create(LoginApi::class.java)
        }
    }
}