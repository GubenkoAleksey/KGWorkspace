package com.hubenko.firestoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Domain
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hubenko.firestoreapp.R
import com.hubenko.firestoreapp.ui.theme.PrimaryActionColor
import com.hubenko.firestoreapp.ui.theme.StatusOfficeLight

@Composable
fun TopStatusCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F7FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = StatusOfficeLight
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.todays_status_part1),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )
                Text(
                    text = stringResource(id = R.string.todays_status_part2),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(220.dp)
            .clickable { onClick() }
            .shadow(16.dp, RoundedCornerShape(40.dp), spotColor = color.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ConfirmationDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_confirmation),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PrimaryActionColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.dialog_status_updated),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryActionColor),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.dialog_ok), color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(id = R.string.nav_home), tint = PrimaryActionColor) },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.History, contentDescription = stringResource(id = R.string.nav_history), tint = Color.Gray) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = stringResource(id = R.string.nav_profile), tint = Color.Gray) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopStatusCardPreview() {
    TopStatusCard()
}

@Preview(showBackground = true)
@Composable
fun StatusCardPreview() {
    StatusCard(title = "Офіс", icon = Icons.Rounded.Domain, color = StatusOfficeLight) {}
}

@Preview(showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog {}
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationPreview() {
    AppBottomNavigation()
}
