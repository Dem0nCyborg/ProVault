package com.example.provault.Biometric


import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BiometricUI(
    navController: NavController,
    promptManager: BiometricPromptManager
) {
    val biometricResult by promptManager.promptResults.collectAsState(initial = null)
    var hasNavigated = remember { mutableStateOf(false) }
    var isPromptShown = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isPromptShown.value) {
            isPromptShown.value = true
            promptManager.showBiometricPrompt(
                title = "Biometric Authentication",
                description = "Authenticate using your biometric data"
            )
        }
    }

    // Handle biometric result
    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess && !hasNavigated.value) {
            hasNavigated.value = true
            navController.popBackStack()
            navController.navigate("sign_in")
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (biometricResult) {
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    (biometricResult as BiometricPromptManager.BiometricResult.AuthenticationError).error
                }
                BiometricPromptManager.BiometricResult.AuthenticationFailed -> "Authentication failed"
                BiometricPromptManager.BiometricResult.AuthenticationSuccess -> "Authentication success"
                BiometricPromptManager.BiometricResult.AuthenticationNotSet -> "Authentication not set"
                BiometricPromptManager.BiometricResult.FeatureUnavailable -> "Feature unavailable"
                BiometricPromptManager.BiometricResult.HardwareUnavailable -> "Hardware unavailable"
                else -> "Waiting for authentication..."
            },
            fontSize = 20.sp
        )
    }
}