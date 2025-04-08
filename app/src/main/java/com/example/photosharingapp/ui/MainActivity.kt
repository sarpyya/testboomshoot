package com.example.photosharingapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.photosharingapp.data.DataService
import com.example.photosharingapp.data.FirebaseDataService
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.ui.navigation.NavigationHost
import com.example.photosharingapp.ui.theme.PhotoSharingAppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataService: DataService = FirebaseDataService() // Usar la implementación de Firebase (Firestore)
        val authRepository = AuthRepository(FirebaseAuth.getInstance())

        setContent {
            PhotoSharingAppTheme {
                MainScreen(dataService, authRepository)
            }
        }
    }
}

@Composable
fun MainScreen(dataService: DataService, authRepository: AuthRepository) {
    val navController = rememberNavController()
    val navBackStackEntryState = navController.currentBackStackEntryAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // Usamos los métodos corregidos para Firestore
            dataService.migrateEventsToFirestore()
            dataService.migrateGroupsToFirestore()
            dataService.migrateRelationshipsToFirestore()
            dataService.migrateUsersToFirestore()
            dataService.migratePostsToFirestore()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val bottomNavItems = listOf(
                    "home" to "Home",
                    "profile" to "Profile",
                    "groups" to "Groups",
                    "events" to "Events",
                    "settings" to "Settings"
                )
                val currentRoute = navBackStackEntryState.value?.destination?.route

                bottomNavItems.forEach { (route, label) ->
                    NavigationBarItem(
                        label = { Text(label) },
                        selected = currentRoute == route,
                        onClick = {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { /* Puedes añadir íconos aquí si los tienes */ }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavigationHost(
            navController = navController,
            paddingValues = paddingValues,
            authRepository = authRepository,
            dataService = dataService,
            onPhotoCaptured = { uri ->
                // Manejar la foto capturada si es necesario
            }
        )
    }
}
