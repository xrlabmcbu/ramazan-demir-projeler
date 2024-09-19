package com.example.appv1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.lightColorScheme


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFA0E3E3),
    secondary = LightBlue,
    background = LightGray,
    surface = White,
    onPrimary = Color(0xFF005B5B),
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun YourAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
