package com.example.erlangga.data.api

import com.example.erlangga.data.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weathercode,windspeed_10m,relative_humidity_2m,apparent_temperature",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
