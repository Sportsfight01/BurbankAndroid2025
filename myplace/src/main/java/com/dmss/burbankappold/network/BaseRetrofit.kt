package com.dmss.burbankappold.network

import android.preference.Preference
import android.util.Base64
import com.dmss.burbankappold.BuildConfig
import common.AppController
import okhttp3.Dns
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.net.Proxy
import java.util.concurrent.TimeUnit
import java.util.prefs.Preferences

object BaseRetrofit {
    private const val CONNECTION_TIMEOUT = 60
    private val isDebugging: Boolean = BuildConfig.DEBUG
    private fun retrofit(baseUrl: String) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okhttpClient())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())

            .build()
    }

    fun depositApiService(baseUrl: String) : DepositApiService {
        return retrofit(baseUrl).create(DepositApiService::class.java)
    }

    private fun okhttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.proxy(Proxy.NO_PROXY)
        httpClient.dns(Dns.SYSTEM)
        httpClient.connectTimeout(
            CONNECTION_TIMEOUT.toLong(),
            TimeUnit.SECONDS
        )
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (isDebugging) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        httpClient.addInterceptor(AuthInterceptor())
        httpClient.addNetworkInterceptor(interceptor)
        return httpClient.build()
    }

    class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val requestBuilder = chain.request().newBuilder()
            println("BASE URL:: "+chain.request().url)
            if (AppController.controller.my_Place_Details != null) {
                val token =
                    "${AppController.controller?.my_Place_Details?.username?.trim()}:${AppController.controller?.my_Place_Details?.password?.trim()}"
                val message: ByteArray = token.toByteArray()
                val encoded = Base64.encodeToString(message, Base64.NO_WRAP)
                val contractNumber = AppController.controller?.my_Place_Details?.jobNumber ?: ""
                requestBuilder.addHeader("Authorization", "Basic $encoded")
                requestBuilder.addHeader("ContractNumber", contractNumber)
            }
            return chain.proceed(requestBuilder.build())
        }
    }

}