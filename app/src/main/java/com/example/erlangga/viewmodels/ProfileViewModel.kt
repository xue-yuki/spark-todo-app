package com.example.erlangga.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erlangga.data.TokenManager
import com.example.erlangga.data.api.RetrofitClient
import com.example.erlangga.data.api.UpdateProfileRequest
import com.example.erlangga.data.api.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileViewModel : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    sealed class ProfileState {
        object Idle : ProfileState()
        object Loading : ProfileState()
        object Success : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getProfile()

                if (response.success && response.data != null) {
                    _currentUser.value = response.data
                    Log.d("ProfileViewModel", "Profile loaded: ${response.data.name}")
                } else {
                    Log.e("ProfileViewModel", "Failed to load profile: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile", e)
            }
        }
    }

    fun updateProfile(name: String? = null, email: String? = null) {
        viewModelScope.launch {
            try {
                _profileState.value = ProfileState.Loading

                val request = UpdateProfileRequest(
                    name = name,
                    email = email
                )

                val response = RetrofitClient.apiService.updateProfile(request)

                if (response.success && response.data != null) {
                    _currentUser.value = response.data

                    // Update saved user info in TokenManager
                    TokenManager.saveUserInfo(response.data.name, response.data.email)

                    _profileState.value = ProfileState.Success
                    Log.d("ProfileViewModel", "Profile updated: ${response.data.name}")
                } else {
                    _profileState.value = ProfileState.Error(response.message ?: "Failed to update profile")
                    Log.e("ProfileViewModel", "Failed to update profile: ${response.message}")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Unknown error")
                Log.e("ProfileViewModel", "Error updating profile", e)
            }
        }
    }

    fun uploadProfilePhoto(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _profileState.value = ProfileState.Loading

                // Get the file from URI
                val file = getFileFromUri(context, imageUri)
                if (file == null) {
                    _profileState.value = ProfileState.Error("Failed to get image file")
                    return@launch
                }

                // Create request body
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestBody)

                // Upload photo
                val response = RetrofitClient.apiService.uploadProfilePhoto(photoPart)

                if (response.success && response.data != null) {
                    // Reload profile to get updated photo
                    loadProfile()

                    _profileState.value = ProfileState.Success
                    Log.d("ProfileViewModel", "Photo uploaded successfully")
                } else {
                    _profileState.value = ProfileState.Error(response.message ?: "Failed to upload photo")
                    Log.e("ProfileViewModel", "Failed to upload photo: ${response.message}")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Unknown error")
                Log.e("ProfileViewModel", "Error uploading photo", e)
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            inputStream?.close()
            tempFile
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error getting file from URI", e)
            null
        }
    }
}
