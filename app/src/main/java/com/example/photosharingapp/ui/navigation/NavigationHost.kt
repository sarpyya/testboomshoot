// ui/navigation/NavigationHost.kt
package com.example.photosharingapp.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.photosharingapp.data.DataService
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.ui.screens.*

@Composable
fun NavigationHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    authRepository: AuthRepository,
    dataService: DataService,
    onPhotoCaptured: (Uri?) -> Unit
) {

    val userId = "testUser"

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                modifier = Modifier.padding(paddingValues),
                dataService = dataService,
                userId = userId
            )
        }
        composable("profile") {
            ProfileScreen(
                modifier = Modifier.padding(paddingValues),
                userId = userId,
                dataService = dataService
            )
        }
        composable("groups") {
            GroupsScreen(
                modifier = Modifier.padding(paddingValues),
                userId = userId,
                dataService = dataService
            )
        }
        composable("settings") {
            SettingsScreen(
                modifier = Modifier.padding(paddingValues),
                authRepository = authRepository,
                onSignOut = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
        composable("preview/{photoUri}") { backStackEntry ->
            val photoUriString = backStackEntry.arguments?.getString("photoUri")
            val photoUri = photoUriString?.let { runCatching { Uri.parse(it) }.getOrNull() }
            if (photoUri != null) {
                PhotoPreviewScreen(
                    photoUri = photoUri,
                    onPostCreated = {
                        navController.popBackStack("home", inclusive = false)
                    },
                    dataService = dataService,
                    authRepository = authRepository
                )
            } else {
                navController.popBackStack("home", inclusive = false)
            }
        }
        composable("camera") {
            CameraScreen(
                onPhotoCaptured = { uri ->
                    onPhotoCaptured(uri)
                },
                onCancel = {
                    navController.popBackStack("home", inclusive = false)
                },
                paddingValues = paddingValues,
                onNavigateToPreview = { uri ->
                    navController.navigate("preview/${Uri.encode(uri.toString())}") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
        // Nueva ruta para EventsScreen
        composable("events") {
            EventsScreen(
                modifier = Modifier.padding(paddingValues),
                dataService = dataService,
                userId = userId
            )
        }
    }
}
