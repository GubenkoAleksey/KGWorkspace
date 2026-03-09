package com.hubenko.firestoreapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hubenko.firestoreapp.R
import com.hubenko.firestoreapp.ui.theme.StatusOfficeLight

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (isAdmin: Boolean) -> Unit
) {
    RoleSelectionContent(onRoleSelected = onRoleSelected)
}

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
            RoleButton(
                text = stringResource(R.string.role_user),
                onClick = { onRoleSelected(false) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            RoleButton(
                text = stringResource(R.string.role_admin),
                onClick = { onRoleSelected(true) }
            )
        }
    }
}

@Composable
fun RoleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusOfficeLight),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, fontSize = 18.sp, color = Color.White)
    }
}
