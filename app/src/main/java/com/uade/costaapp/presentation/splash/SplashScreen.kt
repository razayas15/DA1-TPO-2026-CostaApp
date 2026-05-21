package com.uade.costaapp.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uade.costaapp.presentation.auth.AuthUiState
import com.uade.costaapp.presentation.auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isChecking by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Un ligero delay para asegurar que el usuario alcance a ver el branding
        delay(1200)
        viewModel.checkState()
        isChecking = false
    }

    LaunchedEffect(uiState, isChecking) {
        if (!isChecking) {
            when (uiState) {
                is AuthUiState.Success -> onNavigateToHome()
                is AuthUiState.Idle, is AuthUiState.Error -> onNavigateToLogin()
                AuthUiState.Loading -> { /* Mantener la espera visual */ }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CostaApp",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
