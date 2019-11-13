package com.hibernatev2.appstoredemo.rest.service

import android.content.Context

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class BaseApiClient(private val mContext: Context) {
    private val apiClient: SimpleApiClient
    private val isDebug = true

    init {
        val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

        if (isDebug) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }

        val okHttpClient = okHttpClientBuilder.build()

        val retrofit = Retrofit.Builder()
                .baseUrl(ROOT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

        apiClient = retrofit.create(SimpleApiClient::class.java)
    }

    fun apiClient(): SimpleApiClient {
        return apiClient
    }

    companion object {

        private val ROOT = "https://itunes.apple.com/"
    }
}
