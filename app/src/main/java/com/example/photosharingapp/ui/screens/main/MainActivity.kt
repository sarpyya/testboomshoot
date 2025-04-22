package com.example.photosharingapp.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.example.photosharingapp.BuildConfig
import com.example.photosharingapp.R
import com.example.photosharingapp.data.FirebaseDataService
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.ui.navigation.NavigationHost
import com.example.photosharingapp.ui.theme.PhotoSharingAppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// Sealed class para los iconos de la barra inferior
sealed class NavIcon {
    data class Material(val imageVector: ImageVector) : NavIcon()
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataService = FirebaseDataService()
        val firebaseAuth = FirebaseAuth.getInstance()
        val authRepository = AuthRepository(firebaseAuth)
        val signOut = {
            authRepository.signOut()
            finish()
        }

        // Configurar Cloudinary con credenciales desde BuildConfig
        val config = hashMapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )
        MediaManager.init(this, config)

        setContent {
            PhotoSharingAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?") ?: "login"
                val userId by remember { derivedStateOf { firebaseAuth.currentUser?.uid ?: "" } }
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            navController = navController,
                            onItemClick = {
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        topBar = {
                            if (currentRoute !in listOf("login", "camera", "preview")) {
                                TopAppBar(
                                    title = { Text("PhotoSharingApp") },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                                        }
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if (currentRoute !in listOf("login", "camera", "preview")) {
                                BottomNavBar(
                                    navController = navController,
                                    currentRoute = currentRoute
                                )
                            }
                        },
                        floatingActionButton = {
                            if (currentRoute !in listOf("login", "camera", "preview")) {
                                FloatingActionButton(
                                    onClick = {
                                        navController.navigate("camera?eventId=sampleEvent")
                                    },
                                    modifier = Modifier
                                        .size(56.dp)
                                        .offset(y = (-12).dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_boomshoot_camera),
                                        contentDescription = "Camera",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    ) { paddingValues ->
                        NavigationHost(
                            navController = navController,
                            paddingValues = paddingValues,
                            dataService = dataService,
                            userId = userId,
                            onPhotoCaptured = { uri: Uri? -> /* Manejar captura de fotos */ },
                            onEventCameraClicked = { eventId: String ->
                                navController.navigate("camera?eventId=$eventId")
                            },
                            authRepository = authRepository,
                            onSignOut = signOut
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        Pair("home", NavIcon.Material(Icons.Default.Home)),
        Pair("events", NavIcon.Material(Icons.Default.DateRange))
    )

    NavigationBar(modifier = modifier) {
        navItems.forEach { (route, navIcon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navIcon.imageVector,
                        contentDescription = route,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = if (route == "home") "Inicio" else "Eventos",
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavController,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Nombre de la app
        Text(
            text = "PhotoSharingApp",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Separador
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )

        // Opciones del menú
        DrawerItem(
            label = "Perfil",
            icon = Icons.Default.Star, // Puedes cambiar el ícono si prefieres otro
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
                onItemClick()
            }
        )
        DrawerItem(
            label = "Grupos",
            icon = Icons.Default.Star,
            onClick = {
                navController.navigate("groups") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
                onItemClick()
            }
        )
        DrawerItem(
            label = "Ajustes",
            icon = Icons.Default.Settings,
            onClick = {
                navController.navigate("settings") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
                onItemClick()
            }
        )
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}