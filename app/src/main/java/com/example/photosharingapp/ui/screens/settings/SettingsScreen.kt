package com.example.photosharingapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.navigation.NavController
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.ui.theme.LocalThemeState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authRepository: AuthRepository,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val snackbarHostState = remember { SnackbarHostState() }
    var isSigningOut by remember { mutableStateOf(false) }

    // Tema oscuro desde LocalThemeState
    val isDarkTheme = LocalThemeState.current
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        // Verificar si el usuario ya no está autenticado
        if (FirebaseAuth.getInstance().currentUser == null) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Configuración",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Card para agrupar opciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Opción de tema oscuro
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "Tema oscuro",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tema Oscuro",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Switch(
                            checked = isDarkTheme.value,
                            onCheckedChange = { newValue ->
                                isDarkTheme.value = newValue
                                sharedPreferences.edit { putBoolean("is_dark_theme", newValue) }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (newValue) "Tema oscuro activado" else "Tema claro activado"
                                    )
                                }
                            }
                        )
                    }

                    // Separador
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp, // Reemplazamos COMPILED_CODE por 1.dp
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )

                    // Botón para borrar datos locales
                    SettingsButton(
                        text = "Borrar Datos Locales",
                        icon = Icons.Default.Delete,
                        onClick = {
                            sharedPreferences.edit { clear() }
                            isDarkTheme.value = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Datos locales borrados")
                            }
                        }
                    )

                    // Botón para cerrar sesión
                    SettingsButton(
                        text = "Cerrar Sesión",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        enabled = !isSigningOut,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        onClick = {
                            isSigningOut = true
                            coroutineScope.launch {
                                try {
                                    // Cerrar sesión en Firebase
                                    FirebaseAuth.getInstance().signOut()

                                    // Limpiar credenciales de Google
                                    credentialManager.clearCredentialState(ClearCredentialStateRequest())

                                    // Navegar a login
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    snackbarHostState.showSnackbar("Sesión cerrada")
                                } catch (e: Exception) {
                                    Log.e("SettingsScreen", "Error al cerrar sesión: ${e.message}", e)
                                    snackbarHostState.showSnackbar("Error al cerrar sesión: ${e.message}")
                                } finally {
                                    isSigningOut = false
                                }
                            }
                        }
                    )
                }
            }

            // Indicador de carga durante cierre de sesión
            if (isSigningOut) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun SettingsButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                fontSize = 16.sp,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}