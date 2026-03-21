package com.hubenko.feature.auth.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hubenko.core.ui.theme.CoreTheme

// Імпортуємо R-клас із модуля :core
import com.hubenko.core.R

@Composable
fun AuthHeader(isSignUp: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Тепер завантажуємо логотип напряму, статично та безпечно!
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            contentScale = ContentScale.Fit, // Зберігає пропорції
            modifier = Modifier
                .fillMaxWidth(0.8f) // Займає 80% ширини екрану
                .heightIn(max = 120.dp) // Максимальна висота 120dp
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isSignUp) "Реєстрація" else "Вхід",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isSignUp) "Створіть новий акаунт" else "Вітаємо знову!",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthHeaderLoginPreview() {
    CoreTheme {
        AuthHeader(isSignUp = false)
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthHeaderSignUpPreview() {
    CoreTheme {
        AuthHeader(isSignUp = true)
    }
}
