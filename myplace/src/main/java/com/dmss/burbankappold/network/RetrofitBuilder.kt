package com.dmss.burbankappold.network

import android.text.TextUtils
import common.AppController
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import common.Common
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type

object RetrofitBuilder {
    private const val REQUEST_TIMEOUT = 15

    // private const val BASE_URL = "http://172.17.0.40:7777/api/api/"
    // https://www.burbank.com.au/api/api


    // private const val BASE_URL = "http://dev.burbank.com.au/api/api/"
    // private const val BASE_URL = "http://10.6.45.14:8081/api/api/"
//    private const val BASE_URL = "https://www.burbank.com.au/api/api/"

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private fun getRetrofit(): Retrofit {
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(Common.BaseUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor())
        httpClient.callTimeout(1000, TimeUnit.SECONDS)
        httpClient.readTimeout(1000, TimeUnit.MINUTES)
        httpClient.connectTimeout(1000, TimeUnit.MINUTES)
        httpClient.readTimeout(1000, TimeUnit.MINUTES)
        println("original request url:: "+Common.BaseUrl)

        httpClient.addInterceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            println("original request url:: "+original.url)
            val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
            // adding auth token
           /* val token: String? = CustomSharedPreferences.instance.getToken()
            if (!TextUtils.isEmpty(token)) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }*/
            val request = requestBuilder.build()
            var response = chain.proceed(request)
            response
        }

        return builder.client(httpClient.build()).build()
    }

    val apiService: DepositApiService = getRetrofit().create(DepositApiService::class.java)

    private fun interceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }


}

class IntTypeAdapter : TypeAdapter<Number>() {
    override fun write(out: JsonWriter?, value: Number?) {
        out?.value(value)
    }

    override fun read(`in`: JsonReader?): Number? {
        if (`in`!!.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        return try {
            val result = `in`.nextString()
            if ("" == result) {
                null
            } else result.toInt()
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

}

class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit
    ): Converter<ResponseBody, Any?> {
        val delegate: Converter<ResponseBody, *> =
            retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return Converter<ResponseBody, Any?> { body ->
            if (body.contentLength() == 0L) null else delegate.convert(
                body
            )
        }
    }
}
