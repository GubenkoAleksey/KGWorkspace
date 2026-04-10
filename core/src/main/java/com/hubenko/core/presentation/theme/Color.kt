package com.hubenko.core.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Firebase Colors - Light
val FirebaseBlueLight = Color(0xFF1A73E8)
val FirebaseBackgroundLight = Color(0xFFFFFFFF)
val FirebaseSurfaceLight = Color(0xFFFFFFFF)
val FirebaseOnSurfaceLight = Color(0xFF202124)
val FirebaseOutlineLight = Color(0xFFDADCE0)

// Firebase Colors - Dark
val FirebaseBlueDark = Color(0xFF8AB4F8)
val FirebaseBackgroundDark = Color(0xFF000000)
val FirebaseSurfaceDark = Color(0xFF1E1E1E)
val FirebaseOnSurfaceDark = Color(0xFFE8EAED)
val FirebaseOutlineDark = Color(0xFF3C4043)

val StatusOfficeLight = Color(0xFF26B2C8)
val StatusRemoteLight = Color(0xFFF9B838)
val StatusSickLight = Color(0xFFF05D71)

val ErrorRed = Color(0xFFD32F2F)
val ErrorRedDark = Color(0xFFE57373)

// Text transparency levels for hierarchy
fun ColorScheme.primaryText(): Color = onSurface
fun ColorScheme.secondaryText(): Color = onSurface.copy(alpha = 0.6f)
fun ColorScheme.tertiaryText(): Color = onSurface.copy(alpha = 0.38f)
