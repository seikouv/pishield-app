package com.seikouv.pishield.api

import android.content.Context
import com.seikouv.pishield.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.4.1:8000/"

    private var appContext: Context? = null

    /**
     * Initialize the RetrofitClient with an application context
     * so it can access secure storage for tokens.
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = appContext?.let { TokenManager.getToken(it) }
        
        val requestBuilder = original.newBuilder()
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        
        chain.proceed(requestBuilder.build())
    }

    val api: PiShieldApi by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PiShieldApi::class.java)
    }
}
