package com.example.traveljournal.data.retrofit

import android.content.Context
import com.example.traveljournal.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class ServiceInterceptor(context: Context): Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}