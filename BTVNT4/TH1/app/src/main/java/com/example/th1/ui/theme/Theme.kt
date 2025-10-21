package com.example.th1.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    secondary = Blue300,
    tertiary = LightBlue
)

private val LightColorScheme = lightColorScheme(
    primary = Blue700,
    secondary = Blue500,
    tertiary = Blue300,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = BlueGrey,
    onSurface = BlueGrey
)

@Composable
fun TH1Theme(
    darkTheme: Boolean = false, // Luôn dùng light theme
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Tắt dynamic color để dùng màu custom
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // Luôn dùng light theme với màu trắng

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}