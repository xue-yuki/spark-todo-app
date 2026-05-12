package com.example.erlangga.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erlangga.data.TokenManager
import com.example.erlangga.data.api.GoogleLoginRequest
import com.example.erlangga.data.api.RetrofitClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private var googleSignInClient: GoogleSignInClient? = null

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val userName: String, val userEmail: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun initGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("529331902780-g7lj0g60upm2jf459o2h3s3ocbh7c19s.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInClient(): GoogleSignInClient? = googleSignInClient

    fun handleGoogleSignIn(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val idToken = account.idToken
                if (idToken == null) {
                    _authState.value = AuthState.Error("Failed to get Google token")
                    return@launch
                }

                val response = RetrofitClient.apiService.googleLogin(
                    GoogleLoginRequest(idToken)
                )

                if (response.success && response.data != null) {
                    val loginData = response.data
                    RetrofitClient.setAuthToken(loginData.token)
                    TokenManager.saveUserInfo(loginData.user.name, loginData.user.email)
                    _authState.value = AuthState.Success(
                        userName = loginData.user.name,
                        userEmail = loginData.user.email
                    )
                    Log.d("AuthViewModel", "Google login successful: ${loginData.user.email}")
                } else {
                    val errorMsg = response.message ?: "Login failed"
                    _authState.value = AuthState.Error(errorMsg)
                    Log.e("AuthViewModel", "Google login failed: $errorMsg")
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
                Log.e("AuthViewModel", "Login error", e)
            }
        }
    }

    fun checkSavedAuth() {
        val token = TokenManager.getAuthToken()
        val userName = TokenManager.getUserName()
        val userEmail = TokenManager.getUserEmail()

        if (!token.isNullOrEmpty() && !userName.isNullOrEmpty() && !userEmail.isNullOrEmpty()) {
            // Token exists, restore session
            _authState.value = AuthState.Success(userName, userEmail)
            Log.d("AuthViewModel", "Session restored for: $userEmail")
        } else {
            _authState.value = AuthState.Idle
        }
    }

    fun loginCustom(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val request = com.example.erlangga.data.api.LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.success && response.data != null) {
                    val loginData = response.data
                    RetrofitClient.setAuthToken(loginData.token)
                    TokenManager.saveUserInfo(loginData.user.name, loginData.user.email)
                    _authState.value = AuthState.Success(
                        userName = loginData.user.name,
                        userEmail = loginData.user.email
                    )
                    Log.d("AuthViewModel", "Login successful: ${loginData.user.email}")
                } else {
                    val errorMsg = response.message ?: "Login failed"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: HttpException) {
                val errorMsg = parseHttpError(e) ?: when (e.code()) {
                    401 -> "Email atau password salah"
                    422 -> "Email tidak valid"
                    429 -> "Terlalu banyak percobaan, coba lagi nanti"
                    else -> "Server error (${e.code()})"
                }
                _authState.value = AuthState.Error(errorMsg)
                Log.e("AuthViewModel", "Login HTTP error ${e.code()}", e)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Tidak bisa terhubung ke server")
                Log.e("AuthViewModel", "Login Exception", e)
            }
        }
    }

    private fun parseHttpError(e: HttpException): String? {
        return try {
            val body = e.response()?.errorBody()?.string() ?: return null
            JSONObject(body).getString("message")
        } catch (_: Exception) { null }
    }

    fun signOut() {
        googleSignInClient?.signOut()
        RetrofitClient.setAuthToken(null)
        TokenManager.clearAll()
        _authState.value = AuthState.Idle
    }
}
