package com.hubenko.feature.status.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Domain
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.core.ui.components.AppTopBar
import com.hubenko.core.ui.theme.*
import com.hubenko.feature.status.ui.components.ConfirmationDialog
import com.hubenko.feature.status.ui.components.StatusCard
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StatusScreen(
    viewModel: StatusViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is StatusEffect.NavigateBack -> onNavigateBack()
                is StatusEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (state.isSuccess) {
        ConfirmationDialog(
            onDismiss = { viewModel.onIntent(StatusIntent.DismissDialog) }
        )
    }

    StatusContent(
        isLoading = state.isLoading,
        onStatusSubmit = { status -> viewModel.onIntent(StatusIntent.SubmitStatus(status)) },
        onBackClick = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusContent(
    isLoading: Boolean,
    onStatusSubmit: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Оновити статус",
                onBackClick = onBackClick,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatusCard(
                        title = "Офіс",
                        description = "Працюю безпосередньо в офісі",
                        icon = Icons.Rounded.Domain,
                        color = StatusOfficeLight,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onStatusSubmit("Office") },
                        enabled = !isLoading
                    )
                    StatusCard(
                        title = "Віддалено",
                        description = "Працюю дистанційно (Home Office)",
                        icon = Icons.Rounded.WifiTethering,
                        color = StatusRemoteLight,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onStatusSubmit("Remote") },
                        enabled = !isLoading
                    )
                    StatusCard(
                        title = "Лікарняний",
                        description = "Відсутній через стан здоров'я",
                        icon = Icons.Rounded.HealthAndSafety,
                        color = StatusSickLight,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onStatusSubmit("Sick") },
                        enabled = !isLoading
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Status Content")
@Composable
private fun StatusContentPreview() {
    CoreTheme {
        StatusContent(
            isLoading = false,
            onStatusSubmit = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Status Loading")
@Composable
private fun StatusLoadingPreview() {
    CoreTheme {
        StatusContent(
            isLoading = true,
            onStatusSubmit = {},
            onBackClick = {}
        )
    }
}
