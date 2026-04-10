@file:OptIn(ExperimentalMaterial3Api::class)

package com.hubenko.core.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hubenko.core.R
import com.hubenko.core.presentation.theme.CoreTheme
import com.hubenko.core.presentation.theme.secondaryText

@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onThemeToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondaryText()

            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_back)
                    )
                }
            }
        },
        actions = {
            if (onThemeToggle != null) {
                IconButton(onClick = onThemeToggle) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = stringResource(R.string.cd_toggle_theme),
                        tint = MaterialTheme.colorScheme.secondaryText()
                    )
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppTopBarPreview() {
    CoreTheme {
        AppTopBar(
            title = "Firebase",
            onThemeToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTopBarWithBackPreview() {
    CoreTheme {
        AppTopBar(
            title = "Налаштування",
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTopBarDarkPreview() {
    CoreTheme(darkTheme = true) {
        AppTopBar(
            title = "Firebase",
            onThemeToggle = {}
        )
    }
}
