package com.example.erlangga.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.example.erlangga.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.erlangga.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreenWithGoogle(
    onLoginSuccess: (String, String) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val context = LocalContext.current

    // Initialize Google Sign-In
    LaunchedEffect(Unit) {
        viewModel.initGoogleSignIn(context)
    }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.handleGoogleSignIn(account)
            } catch (e: ApiException) {
                // Handle error
            }
        }
    }

    // Observe auth state
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            val success = authState as AuthViewModel.AuthState.Success
            onLoginSuccess(success.userName, success.userEmail)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                drawGridBackground(isSystemDark)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp)
                .padding(top = 40.dp, bottom = 30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Brand mark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.onBackground,
                            RoundedCornerShape(9.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.background
                    )
                }
                Text(
                    text = "Spark",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Heading
            Column(
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Welcome back.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.7).sp,
                    lineHeight = 35.2.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Let's pick up the streak.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.7).sp,
                    lineHeight = 35.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Show error if any
            if (authState is AuthViewModel.AuthState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (authState as AuthViewModel.AuthState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }
            }

            // Input fields (disabled for now, just for UI)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 18.dp)
            ) {
                CustomInputField(
                    label = "EMAIL",
                    value = email,
                    onValueChange = { email = it },
                    visualTransformation = VisualTransformation.None
                )

                CustomInputField(
                    label = "PASSWORD",
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    trailingText = "Forgot?"
                )
            }

            // Sign in button
            Button(
                onClick = { 
                    if (email.isNotBlank() && password.isNotBlank()) {
                        viewModel.loginCustom(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = authState !is AuthViewModel.AuthState.Loading
            ) {
                if (authState is AuthViewModel.AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.background
                    )
                } else {
                    Text(
                        text = "Sign in",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp
                    )
                }
            }

            // Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "OR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Google sign-in button
            OutlinedButton(
                onClick = {
                    val signInIntent = viewModel.getGoogleSignInClient()?.signInIntent
                    signInIntent?.let { googleSignInLauncher.launch(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp),
                enabled = authState !is AuthViewModel.AuthState.Loading
            ) {
                if (authState is AuthViewModel.AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (authState is AuthViewModel.AuthState.Loading)
                        "Signing in..."
                    else
                        "Sign in with Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New here? ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Create an account",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
        }
    }
}
