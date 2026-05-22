package com.uade.costaapp.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        delay(1200)
        viewModel.checkState()
        isChecking = false
    }

    LaunchedEffect(uiState, isChecking) {
        if (!isChecking) {
            when (uiState) {
                is AuthUiState.Success -> onNavigateToHome()
                is AuthUiState.Idle, is AuthUiState.Error -> onNavigateToLogin()
                AuthUiState.Loading -> { }
            }
        }
    }

    // Branding Colors
    val darkBlue = Color(0xFF0A1D37)
    val brandOrange = Color(0xFFE05E36)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBlue)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(brandOrange, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "CostaApp",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu hogar en Pinamar",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
