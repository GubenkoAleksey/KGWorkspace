package com.hubenko.feature.auth.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme

/**
 * Семантичний компонент для основних дій авторизації (кнопка входу/реєстрації та перемикач режимів)
 */
@Composable
fun AuthSubmitActions(
    isSignUp: Boolean,
    isLoading: Boolean,
    onActionSubmit: () -> Unit,
    onToggleAuthMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onActionSubmit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (isSignUp) "Зареєструватися" else "Увійти")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onToggleAuthMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isSignUp) "Вже є акаунт? Увійти" 
                    else "Немає акаунту? Створити"
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Actions")
@Composable
private fun AuthSubmitActionsLoginPreview() {
    CoreTheme {
        AuthSubmitActions(
            isSignUp = false,
            isLoading = false,
            onActionSubmit = {},
            onToggleAuthMode = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading Actions")
@Composable
private fun AuthSubmitActionsLoadingPreview() {
    CoreTheme {
        AuthSubmitActions(
            isSignUp = false,
            isLoading = true,
            onActionSubmit = {},
            onToggleAuthMode = {}
        )
    }
}
