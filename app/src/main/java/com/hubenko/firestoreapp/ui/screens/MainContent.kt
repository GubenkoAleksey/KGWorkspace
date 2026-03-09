package com.hubenko.firestoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Domain
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.firestoreapp.R
import com.hubenko.firestoreapp.ui.theme.*
import com.hubenko.firestoreapp.ui.viewmodel.StatusUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    uiState: StatusUiState,
    onStatusSubmit: (String) -> Unit,
    onDismissDialog: () -> Unit
) {
    if (uiState is StatusUiState.Success) {
        ConfirmationDialog(
            onDismiss = onDismissDialog
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { AppBottomNavigation() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle FAB click */ },
                shape = CircleShape,
                containerColor = PrimaryActionColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.action_add))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE0F7FA),
                            Color(0xFFFCE4EC)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                TopStatusCard()

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusCard(
                        title = stringResource(id = R.string.status_office),
                        icon = Icons.Rounded.Domain,
                        color = StatusOfficeLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onStatusSubmit("Office") }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatusCard(
                        title = stringResource(id = R.string.status_remote),
                        icon = Icons.Rounded.WifiTethering,
                        color = StatusRemoteLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onStatusSubmit("Remote") }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatusCard(
                        title = stringResource(id = R.string.status_sick),
                        icon = Icons.Rounded.HealthAndSafety,
                        color = StatusSickLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onStatusSubmit("Sick") }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    MainContent(
        uiState = StatusUiState.Idle,
        onStatusSubmit = {},
        onDismissDialog = {}
    )
}
