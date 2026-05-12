package com.example.erlangga.data.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: CurrentWeatherData
)

data class CurrentWeatherData(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("weathercode") val weatherCode: Int,
    @SerializedName("windspeed_10m") val windSpeed: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Double
)
