package com.example.erlangga.data.api

import com.example.erlangga.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // TODO: Replace with your Laravel API URL
    // Development (Emulator): http://10.0.2.2:8000/api/
    // Development (Real Device): http://10.212.68.24:8000/api/
    // Production: https://your-domain.com/api/
    private const val BASE_URL = "http://10.212.68.24:8000/api/"

    fun setAuthToken(token: String?) {
        if (token != null) {
            TokenManager.saveAuthToken(token)
        } else {
            TokenManager.clearAll()
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        // Add auth token if available
        TokenManager.getAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        // Add common headers
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

/**
 * Usage Example:
 *
 * // Login
 * val response = RetrofitClient.apiService.login(
 *     LoginRequest("maya@example.com", "password123")
 * )
 *
 * if (response.success) {
 *     RetrofitClient.setAuthToken(response.data?.token)
 *     // Navigate to home
 * }
 *
 * // Get tasks
 * val tasksResponse = RetrofitClient.apiService.getTasks()
 * if (tasksResponse.success) {
 *     val tasks = tasksResponse.data ?: emptyList()
 *     // Update UI
 * }
 */
