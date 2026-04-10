package com.hubenko.feature.auth.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.presentation.theme.CoreTheme

/**
 * Кнопка підтвердження входу в систему.
 */
@Composable
fun LoginSubmitButton(
    isLoading: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Button(
            onClick = onSubmit,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Увійти")
        }
    }
}

@Preview(showBackground = true, name = "Default")
@Composable
private fun LoginSubmitButtonPreview() {
    CoreTheme {
        LoginSubmitButton(isLoading = false, onSubmit = {})
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun LoginSubmitButtonLoadingPreview() {
    CoreTheme {
        LoginSubmitButton(isLoading = true, onSubmit = {})
    }
}

