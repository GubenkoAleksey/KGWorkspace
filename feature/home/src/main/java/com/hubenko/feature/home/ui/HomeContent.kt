package com.hubenko.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.components.ProjectItem
import com.hubenko.core.ui.theme.CoreTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Firebase",
                userInitial = "A", // Можна буде брати з профілю
                actions = {
                    IconButton(onClick = { onIntent(HomeIntent.OnLogoutClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Вийти"
                        )
                    }
                }
            )
        },
        snackbarHost = { snackbarHost() }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProjectItem(
                    title = "Відправити статус",
                    subtitle = "Оновіть свій поточний стан роботи",
                    leadingIcon = Icons.AutoMirrored.Filled.Send,
                    onClick = { onIntent(HomeIntent.OnSendStatusClick) }
                )

                if (state.isAdmin) {
                    ProjectItem(
                        title = "Панель адміністратора",
                        subtitle = "Керування співробітниками та звітами",
                        leadingIcon = Icons.Default.AdminPanelSettings,
                        onClick = { onIntent(HomeIntent.OnAdminPanelClick) }
                    )
                }

                ProjectItem(
                    title = "Тестове сповіщення",
                    subtitle = "Перевірте роботу системи push-повідомлень",
                    leadingIcon = Icons.Default.NotificationsActive,
                    onClick = { onIntent(HomeIntent.OnTestNotificationClick) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

@Preview(name = "Admin View - Light", showBackground = true)
@Composable
private fun HomeContentAdminLightPreview() {
    CoreTheme(darkTheme = false) {
        HomeContent(
            state = HomeState(isAdmin = true, isLoading = false),
            onIntent = {}
        )
    }
}

@Preview(name = "Admin View - Dark", showBackground = true)
@Composable
private fun HomeContentAdminDarkPreview() {
    CoreTheme(darkTheme = true) {
        HomeContent(
            state = HomeState(isAdmin = true, isLoading = false),
            onIntent = {}
        )
    }
}
