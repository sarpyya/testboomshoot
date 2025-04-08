// ui/theme/Theme.kt (nuevo archivo)
package com.example.photosharingapp.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider

// Definir el CompositionLocal para el estado del tema
val LocalThemeState = compositionLocalOf { mutableStateOf(false) }

@Composable
fun PhotoSharingAppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // Leer el estado inicial del tema desde SharedPreferences
    val isDarkTheme = remember {
        mutableStateOf(sharedPreferences.getBoolean("is_dark_theme", false))
    }

    // Proveer el estado del tema a toda la app
    CompositionLocalProvider(LocalThemeState provides isDarkTheme) {
        MaterialTheme(
            colorScheme = if (isDarkTheme.value) darkColorScheme() else lightColorScheme()
        ) {
            content()
        }
    }
}