package com.example.erlangga.viewmodels

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.erlangga.data.api.WeatherApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import kotlin.math.roundToInt

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Idle)
    val weatherState: StateFlow<WeatherState> = _weatherState

    private val weatherService: WeatherApiService = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiService::class.java)

    sealed class WeatherState {
        object Idle : WeatherState()
        object Loading : WeatherState()
        data class Success(
            val temperature: Int,
            val feelsLike: Int,
            val humidity: Int,
            val windSpeed: Double,
            val description: String,
            val emoji: String,
            val cityName: String
        ) : WeatherState()
        data class Error(val message: String) : WeatherState()
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _weatherState.value = WeatherState.Loading
                val response = weatherService.getForecast(lat, lon)
                val current = response.current
                val cityName = getCityName(lat, lon)
                val (desc, emoji) = describeWeatherCode(current.weatherCode)
                _weatherState.value = WeatherState.Success(
                    temperature = current.temperature.roundToInt(),
                    feelsLike = current.apparentTemperature.roundToInt(),
                    humidity = current.humidity,
                    windSpeed = current.windSpeed,
                    description = desc,
                    emoji = emoji,
                    cityName = cityName
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Failed to fetch weather")
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getCityName(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val addr = addresses?.firstOrNull()
            addr?.locality
                ?: addr?.subLocality
                ?: addr?.subAdminArea
                ?: addr?.adminArea
                ?: "Here"
        } catch (e: Exception) {
            "Here"
        }
    }

    private fun describeWeatherCode(code: Int): Pair<String, String> = when (code) {
        0 -> "Clear sky" to "☀️"
        1 -> "Mainly clear" to "🌤️"
        2 -> "Partly cloudy" to "⛅"
        3 -> "Overcast" to "☁️"
        in 45..48 -> "Foggy" to "🌫️"
        in 51..55 -> "Drizzle" to "🌦️"
        in 61..65 -> "Rain" to "🌧️"
        in 71..77 -> "Snow" to "❄️"
        in 80..82 -> "Showers" to "🌧️"
        in 85..86 -> "Snow showers" to "🌨️"
        95 -> "Thunderstorm" to "⛈️"
        in 96..99 -> "Thunderstorm" to "⛈️"
        else -> "Unknown" to "🌡️"
    }
}
