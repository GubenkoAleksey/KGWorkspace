package com.hubenko.feature.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
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
    onIntent: (HomeIntent) -> Unit
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
        }
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
