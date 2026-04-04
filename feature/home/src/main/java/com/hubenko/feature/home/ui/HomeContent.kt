package com.hubenko.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.components.PrimaryActionButton
import com.hubenko.core.ui.theme.CoreTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Головне меню",
                actions = {
                    IconButton(onClick = { onIntent(HomeIntent.OnLogoutClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Вийти"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(screenHeight * 0.125f))

                PrimaryActionButton(
                    text = "Відправити статус",
                    onClick = { onIntent(HomeIntent.OnSendStatusClick) }
                )

                if (state.isAdmin) {
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryActionButton(
                        text = "Панель адміністратора",
                        onClick = { onIntent(HomeIntent.OnAdminPanelClick) }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { onIntent(HomeIntent.OnTestNotificationClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Тестовий сигнал сповіщення")
                }
            }
        }
    }
}

@Preview(name = "Admin View", showBackground = true)
@Composable
private fun HomeContentAdminPreview() {
    CoreTheme {
        HomeContent(
            state = HomeState(isAdmin = true, isLoading = false),
            onIntent = {}
        )
    }
}

@Preview(name = "User View", showBackground = true)
@Composable
private fun HomeContentUserPreview() {
    CoreTheme {
        HomeContent(
            state = HomeState(isAdmin = false, isLoading = false),
            onIntent = {}
        )
    }
}

@Preview(name = "Loading View", showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    CoreTheme {
        HomeContent(
            state = HomeState(isLoading = true),
            onIntent = {}
        )
    }
}
