package com.hubenko.feature.home.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.core.ui.components.AppTopBar
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

    HomeContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

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
fun HomeContentAdminPreview() {
    CoreTheme {
        HomeContent(
            state = HomeState(isAdmin = true, isLoading = false),
            onIntent = {}
        )
    }
}

@Preview(name = "User View", showBackground = true)
@Composable
fun HomeContentUserPreview() {
    CoreTheme {
        HomeContent(
            state = HomeState(isAdmin = false, isLoading = false),
            onIntent = {}
        )
    }
}

@Preview(name = "Loading View", showBackground = true)
@Composable
fun HomeContentLoadingPreview() {
    CoreTheme {
        HomeContent(
            state = HomeState(isLoading = true),
            onIntent = {}
        )
    }
}
