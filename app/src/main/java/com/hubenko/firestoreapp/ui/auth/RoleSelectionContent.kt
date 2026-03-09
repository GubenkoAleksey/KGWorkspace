package com.hubenko.firestoreapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hubenko.firestoreapp.R
import com.hubenko.firestoreapp.ui.components.PrimaryActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionContent(
    onRoleSelected: (isAdmin: Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_role_title), fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PrimaryActionButton(
                text = stringResource(R.string.role_user),
                onClick = { onRoleSelected(false) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryActionButton(
                text = stringResource(R.string.role_admin),
                onClick = { onRoleSelected(true) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionContentPreview() {
    RoleSelectionContent(onRoleSelected = {})
}
