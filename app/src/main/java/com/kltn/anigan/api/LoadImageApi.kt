package com.kltn.anigan.api

import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.kltn.anigan.api.BaseURL.BaseURL.BASE_URL
import com.kltn.anigan.domain.response.LoadImageResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.lang.reflect.Type
import java.net.URL


interface LoadImageApi {
    @GET("/image")
    fun getRefImage(@Query("type") type: Int, @Query("limit") limit: Int = 5): Call<LoadImageResponse>

    companion object {
        operator fun invoke(): LoadImageApi{
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Set log level to BODY to log request and response data

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor) // Add the logging interceptor
                .build()

            val gson = GsonBuilder()
                .registerTypeAdapter(Url::class.java, UrlDeserializer())
                .registerTypeAdapter(Url::class.java, UrlInstanceCreator())
                .create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(LoadImageApi::class.java)
        }
    }
}

class UrlInstanceCreator : InstanceCreator<Url> {
    override fun createInstance(type: Type?): Url {
        return Url() // You can pass a default URL here if needed
    }
}
class UrlDeserializer : JsonDeserializer<URL> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): URL {
        if (json == null || json.isJsonNull) {
            throw JsonParseException("URL is null")
        }
        return URL(json.asString)
    }
}
