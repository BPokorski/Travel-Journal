package com.example.traveljournal.data

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class AppGlideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val builder = OkHttpClient.Builder()

        builder
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)

        val okHttpClient = builder.build()

        registry.replace(GlideUrl::class.java, InputStream::class.java,
                OkHttpUrlLoader.Factory(okHttpClient))
    }
}