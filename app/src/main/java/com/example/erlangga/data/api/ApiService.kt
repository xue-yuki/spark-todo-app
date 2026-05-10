package com.example.erlangga.data.api

import com.example.erlangga.data.models.Task
import retrofit2.http.*

// API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

// Auth responses
data class LoginResponse(
    val user: User,
    val token: String
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val profile_photo: String? = null
)

// Auth requests
data class GoogleLoginRequest(
    val id_token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

// Profile requests
data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val profile_photo: String? = null
)

// Task requests
data class CreateTaskRequest(
    val title: String,
    val tag: String,
    val time: String,
    val due_date: String? = null,
    val priority: String,
    val notes: String? = null
)

data class UpdateTaskRequest(
    val title: String,
    val tag: String,
    val time: String,
    val done: Boolean,
    val priority: String,
    val due_date: String? = null,
    val notes: String? = null
)

// Analytics response
data class AnalyticsData(
    val total_shipped: Int,
    val total_tasks: Int,
    val completion_rate: Int,
    val streak_days: Int,
    val focus_time_hours: Double,
    val tasks_per_day: List<Int>,
    val heatmap_data: List<Int>,
    val tasks_by_tag: Map<String, Int> = emptyMap()
)

/**
 * Retrofit API Service Interface
 *
 * TODO: Update BASE_URL in RetrofitClient.kt with your Laravel API URL
 */
interface ApiService {

    // Authentication Endpoints
    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): ApiResponse<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<LoginResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @GET("auth/user")
    suspend fun getCurrentUser(): ApiResponse<User>

    // Task Endpoints
    @GET("tasks")
    suspend fun getTasks(
        @Query("filter") filter: String? = null,
        @Query("tag") tag: String? = null,
        @Query("date") date: String? = null
    ): ApiResponse<List<Task>>

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") id: Int): ApiResponse<Task>

    @POST("tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): ApiResponse<Task>

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body request: UpdateTaskRequest
    ): ApiResponse<Task>

    @POST("tasks/{id}/toggle")
    suspend fun toggleTaskStatus(@Path("id") id: Int): ApiResponse<Task>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): ApiResponse<Unit>

    // Analytics Endpoints
    @GET("analytics")
    suspend fun getAnalytics(@Query("period") period: String? = "today"): ApiResponse<AnalyticsData>

    @GET("analytics/history")
    suspend fun getTaskHistory(@Query("days") days: Int? = 7): ApiResponse<List<Any>>

    // Profile Endpoints
    @GET("profile")
    suspend fun getProfile(): ApiResponse<User>

    @PUT("profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<User>

    @Multipart
    @POST("profile/photo")
    suspend fun uploadProfilePhoto(
        @Part photo: okhttp3.MultipartBody.Part
    ): ApiResponse<Map<String, String>>

    @POST("profile/password")
    suspend fun changePassword(
        @Body request: Map<String, String>
    ): ApiResponse<Unit>
}
