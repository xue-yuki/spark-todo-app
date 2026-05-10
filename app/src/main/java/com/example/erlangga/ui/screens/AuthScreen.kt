package com.example.erlangga.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.erlangga.R

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("maya@spark.app") }
    var password by remember { mutableStateOf("••••••••") }
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()

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
                    Icon(Icons.Default.Check,
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

            // Input fields
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 18.dp)
            ) {
                // Email input
                CustomInputField(
                    label = "EMAIL",
                    value = email,
                    onValueChange = { email = it },
                    visualTransformation = VisualTransformation.None
                )

                // Password input
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
                onClick = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Sign in",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp
                )
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
                onClick = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = androidx.compose.ui.graphics.Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sign in with Google",
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
                    modifier = Modifier.clickable { /* TODO: Navigate to register */ }
                )
            }
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingText: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Label row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (trailingText != null) {
                    Text(
                        text = trailingText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { /* TODO: Forgot password */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Input field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                ),
                visualTransformation = visualTransformation,
                singleLine = true
            )
        }
    }
}

fun DrawScope.drawGridBackground(isDark: Boolean) {
    val dotColor = if (isDark) Color(0x12EDEAE0) else Color(0x0F1A1A1A)
    val lineColor = if (isDark) Color(0x0AEDEAE0) else Color(0x091A1A1A)

    val cellSize = 28.dp.toPx()
    val width = size.width
    val height = size.height

    // Create gradient mask
    val gradientBrush = Brush.radialGradient(
        colors = listOf(
            Color.Black.copy(alpha = 1f),
            Color.Black.copy(alpha = 0.6f),
            Color.Transparent
        ),
        center = Offset(width / 2, height * 0.3f),
        radius = width * 0.7f
    )

    // Draw vertical lines
    var x = 0f
    while (x <= width) {
        drawLine(
            color = lineColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1.dp.toPx()
        )
        x += cellSize
    }

    // Draw horizontal lines
    var y = 0f
    while (y <= height) {
        drawLine(
            color = lineColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1.dp.toPx()
        )
        y += cellSize
    }

    // Draw dots at intersections
    x = cellSize / 2
    while (x <= width) {
        y = cellSize / 2
        while (y <= height) {
            drawCircle(
                color = dotColor,
                radius = 1.2.dp.toPx(),
                center = Offset(x, y)
            )
            y += cellSize
        }
        x += cellSize
    }
}
