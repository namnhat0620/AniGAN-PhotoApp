package com.kltn.anigan.api

import com.kltn.anigan.domain.UploadResponse
import okhttp3.OkHttpClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call

interface UploadApi {
    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("desc") desc: RequestBody
    ): Call<UploadResponse>

    companion object {
        operator fun invoke(): UploadApi{
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Set log level to BODY to log request and response data

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor) // Add the logging interceptor
                .build()

            return Retrofit.Builder()
                .baseUrl("https://f996-35-224-173-219.ngrok-free.app/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadApi::class.java)
        }
    }
}