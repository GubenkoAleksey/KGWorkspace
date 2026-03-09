package com.hubenko.firestoreapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.firestoreapp.ui.components.PrimaryActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuContent(
    isAdmin: Boolean,
    onNavigateToStatus: () -> Unit,
    onAdminPanelClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Головне меню", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
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
                onClick = onNavigateToStatus
            )

            if (isAdmin) {
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryActionButton(
                    text = "Панель адміністратора",
                    onClick = onAdminPanelClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeMenuContentPreview() {
    HomeMenuContent(
        isAdmin = true,
        onNavigateToStatus = {},
        onAdminPanelClick = {}
    )
}
