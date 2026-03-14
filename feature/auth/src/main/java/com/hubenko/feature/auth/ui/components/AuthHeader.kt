package com.hubenko.feature.auth.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hubenko.core.ui.theme.CoreTheme

@Composable
fun AuthHeader(isSignUp: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
