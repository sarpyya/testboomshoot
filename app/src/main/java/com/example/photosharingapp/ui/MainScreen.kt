// ui/MainScreen.kt
package com.example.photosharingapp.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.photosharingapp.data.DataService
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.ui.navigation.BottomNavigationBar
import com.example.photosharingapp.ui.navigation.NavigationHost
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    photoTakenUri: Uri?,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
    authRepository: AuthRepository,
    dataService: DataService,
    navigateToCamera: () -> Unit,
    updatePhotoTakenUri: (Uri?) -> Unit // Nuevo parámetro
) {
    var navigated by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Manejar la navegación a la pantalla de vista previa cuando se toma una foto
    LaunchedEffect(photoTakenUri) {
        if (photoTakenUri != null && !navigated) {
            Log.d("MainScreen", "Navigating to preview with photoTakenUri=$photoTakenUri")
            navController.navigate("preview/${Uri.encode(photoTakenUri.toString())}") {
                popUpTo("home") { inclusive = false }
            }
            navigated = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onProfileClick = {
                    navController.navigate("profile")
                    scope.launch { drawerState.close() }
                },
                onGroupsClick = {
                    navController.navigate("groups")
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PhotoSharingApp") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onFabClick() // Solicitar permisos
                        navigateToCamera() // Navegar a CameraScreen
                    },
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Take Photo")
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            NavigationHost(
                navController = navController,
                paddingValues = paddingValues,
                authRepository = authRepository,
                dataService = dataService,
                onPhotoCaptured = { uri ->
                    updatePhotoTakenUri(uri) // Usar la función para actualizar
                    navigated = false // Resetear para permitir nueva navegación
                }
            )
        }
    }
}

@Composable
fun DrawerContent(
    onProfileClick: () -> Unit,
    onGroupsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Menú",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = onProfileClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Perfil")
        }
        Button(
            onClick = onGroupsClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Grupos")
        }
    }
}