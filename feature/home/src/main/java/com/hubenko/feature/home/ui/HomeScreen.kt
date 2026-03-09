package com.hubenko.feature.home.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.core.ui.components.PrimaryActionButton
import com.hubenko.core.ui.theme.CoreTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToStatus: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.NavigateToStatus -> onNavigateToStatus()
                is HomeEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    HomeMenuContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val backgroundColor = Color.White

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Головне меню", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
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
            return@Scaffold
        }

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

@Preview(name = "Admin View", showBackground = true)
@Composable
fun HomeMenuContentAdminPreview() {
    CoreTheme {
        HomeMenuContent(
            state = HomeState(isAdmin = true, isLoading = false),
            onIntent = {}
        )
    }
}

@Preview(name = "User View", showBackground = true)
@Composable
fun HomeMenuContentUserPreview() {
    CoreTheme {
        HomeMenuContent(
            state = HomeState(isAdmin = false, isLoading = false),
            onIntent = {}
        )
    }
}
