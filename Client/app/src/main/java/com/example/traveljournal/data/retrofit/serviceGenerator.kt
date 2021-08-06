package com.example.traveljournal.data.retrofit

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    private const val API_BASE_URL = "BASE_URL"
//    private val httpClient = OkHttpClient.Builder()
    private val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>?, context: Context): S {
        val retrofit =
                builder.client(okHttpClient(context)).build()
        return retrofit.create(serviceClass)
    }
    private fun okHttpClient(context: Context):OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(ServiceInterceptor(context))
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build()
    }
}