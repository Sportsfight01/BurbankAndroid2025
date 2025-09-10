package com.dmss.burbankapp.data.api

import android.annotation.SuppressLint
import android.text.TextUtils
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import common.AppController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


object RetrofitBuilder {
    private const val REQUEST_TIMEOUT = 15
     private var retryCount=0
    // private const val BASE_URL = "http://172.17.0.40:7777/api/api/"
    // https://www.burbank.com.au/api/api
//     public const val BASE_URL = "http://10.6.45.14:8085/api-v2/api/"

      // used before v3 (version 3.6)
    // private const val BASE_URL = "http://dev.burbank.com.au/api/api/"

    // using from v3 (version 3.7)
     const val BASE_URL = "https://www.burbank.com.au/api-v2/api/"

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    @SuppressLint("SuspiciousIndentation")
    private fun getRetrofit(): Retrofit {
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))



        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
//        httpClient.addInterceptor(interceptor())
//        httpClient.addInterceptor(AuthInterceptor())

        httpClient.callTimeout(1000, TimeUnit.SECONDS)
        httpClient.readTimeout(1000, TimeUnit.MINUTES)
        httpClient.connectTimeout(1000, TimeUnit.MINUTES)
        httpClient.readTimeout(1000, TimeUnit.MINUTES)
        httpClient.retryOnConnectionFailure(true)
//        httpClient.addInterceptor(LogJsonInterceptor())


  httpClient.addInterceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
            // adding auth token
            val token: String? = CustomSharedPreferences.instance.getToken()
            if (!TextUtils.isEmpty(token)) {
                Timber.e("TOKEN ${token}")
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            val request = requestBuilder.build()
            var response = chain.proceed(request)
              println("response code:: "+response.code+" Request url:: "+request.url)
            if(response.code==200){
             println("response code:: DATA:: "+request.url+" body::"+Gson().toJson(request.body))
            }

           if (response.code == 401) {
               var newToken=""
                val jsonObject = JsonObject()
                jsonObject.addProperty("Username", AppController.AuthUserName)
                jsonObject.addProperty("Password", AppController.AuthPassword)

               val result: String = AppController.controller.webApiCall().postData_to_MyPlace(BASE_URL+"Account/Authenticate", jsonObject.toString())
               val jsonObject1 = JSONObject(result)
               newToken=jsonObject1.getString("Token")
               println("New Token1:: "+newToken)
               CustomSharedPreferences.instance.saveToken(newToken)
               val originalRequest = chain.request()
               val authorisedRequestBuilder = originalRequest.newBuilder()

               val modifiedRequest = authorisedRequestBuilder
                   .addHeader("Accept", "application/json")
                   .header("Authorization", "Bearer $newToken").build()
//               val retryOtherresponse= chain.proceed(modifiedRequest)
                 response.close()
               response=chain.proceed(modifiedRequest)

            }
            response
        }


        return builder.client(httpClient.build()).build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)




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

