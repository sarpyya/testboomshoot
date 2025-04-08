// ui/screens/ProfileScreen.kt
package com.example.photosharingapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.photosharingapp.data.DataService
import com.example.photosharingapp.model.Users
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    dataService: DataService,
    userId: String
) {
    val coroutineScope = rememberCoroutineScope()
    var users by remember { mutableStateOf<Users?>(null) }
    var postCount by remember { mutableStateOf(0) }
    var groupCount by remember { mutableStateOf(0) }

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Obtener el usuario
            users = dataService.getUser(userId)
            // Contar los posts del usuario
            postCount = dataService.getPosts().count { it.userId == userId }
            // Contar los grupos del usuario
            groupCount = dataService.getUserGroups(userId).size
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Perfil",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (users == null) {
            Text(
                text = "Cargando perfil...",
                fontSize = 16.sp,
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
            )
        } else {
            // Foto de perfil
            users?.profilePicture?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(model = url),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            } ?: run {
                // Placeholder si no hay foto de perfil
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = users?.username?.firstOrNull()?.toString() ?: "U",
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre de usuario
            Text(
                text = users?.username ?: "Usuario",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Correo electrónico
            Text(
                text = users?.email ?: "Sin correo",
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Posts", value = postCount.toString())
                StatItem(label = "Grupos", value = groupCount.toString())
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodySmall
        )
    }
}