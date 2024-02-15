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
    @POST("upload/user")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("reference_image_url") referenceImageUrl: RequestBody
    ): Call<UploadResponse>

    companion object {
        operator fun invoke(): UploadApi{
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Set log level to BODY to log request and response data

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor) // Add the logging interceptor
                .build()

            return Retrofit.Builder()
                .baseUrl("https://anigan-be-production.up.railway.app/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadApi::class.java)
        }
    }
}