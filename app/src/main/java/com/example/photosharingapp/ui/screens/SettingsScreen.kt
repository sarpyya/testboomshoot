// ui/screens/SettingsScreen.kt
package com.example.photosharingapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.ui.theme.LocalThemeState

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    authRepository: AuthRepository,
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // Obtener el estado del tema desde LocalThemeState
    val isDarkTheme = LocalThemeState.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tema Oscuro",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isDarkTheme.value,
                onCheckedChange = { newValue ->
                    isDarkTheme.value = newValue
                    sharedPreferences.edit().putBoolean("is_dark_theme", newValue).apply()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                sharedPreferences.edit().clear().apply()
                isDarkTheme.value = false
                android.widget.Toast.makeText(
                    context,
                    "Datos locales borrados",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Borrar Datos Locales", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authRepository.signOut()
                onSignOut()
                android.widget.Toast.makeText(
                    context,
                    "Sesión cerrada",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Cerrar Sesión", fontSize = 16.sp, color = MaterialTheme.colorScheme.onError)
        }
    }
}