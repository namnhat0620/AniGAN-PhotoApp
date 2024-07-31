package com.kltn.anigan.api

import com.kltn.anigan.api.BaseURL.BaseURL.BASE_URL
import com.kltn.anigan.domain.response.UploadUserImageResponse
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
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

interface UploadApi {
    @Multipart
    @POST("upload/user")
    fun uploadImage(
        @Header("Authorization") token: String?,
        @Part image: MultipartBody.Part,
        @Part("reference_image_url") referenceImageUrl: RequestBody,
        @Part("mobile_id") mobileId: RequestBody
    ): Call<UploadUserImageResponse>

    companion object {
        operator fun invoke(): UploadApi{
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Set log level to BODY to log request and response data

            val client = OkHttpClient.Builder()
//                .addInterceptor(interceptor) // Add the logging interceptor
                .connectTimeout(20, TimeUnit.SECONDS) // Example: Set connect timeout to 10 seconds
                .readTimeout(20, TimeUnit.SECONDS) // Example: Set read timeout to 10 seconds
                .writeTimeout(20, TimeUnit.SECONDS) // Example: Set write timeout to 10 seconds
                .build()


            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadApi::class.java)
        }
    }
}