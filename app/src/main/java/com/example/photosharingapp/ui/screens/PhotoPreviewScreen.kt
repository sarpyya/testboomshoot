// ui/screens/PhotoPreviewScreen.kt
package com.example.photosharingapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.photosharingapp.data.DataService
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.model.Group
import com.example.photosharingapp.model.Post
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhotoPreviewScreen(
    photoUri: Uri?,
    onPostCreated: () -> Unit,
    dataService: DataService,
    authRepository: AuthRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var content by remember { mutableStateOf("Foto compartida") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var eventId by remember { mutableStateOf("") }
    var showGroupDropdown by remember { mutableStateOf(false) }
    var showEventInput by remember { mutableStateOf(false) }
    var groups by remember { mutableStateOf(listOf<Group>()) }

    // Cargar los grupos del usuario
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val userId =  "testUser"
            groups = dataService.getUserGroups(userId)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen a pantalla completa
        if (photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = photoUri),
                contentDescription = "Foto tomada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                "No se pudo cargar la imagen",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Controles en la parte inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo de descripción
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                enabled = !isLoading
            )

            // Botones de publicación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón para postear en el perfil
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            createPost(
                                content = content,
                                photoUri = photoUri,
                                groupId = null,
                                eventId = null,
                                dataService = dataService,
                                authRepository = authRepository,
                                onPostCreated = onPostCreated
                            )
                            isLoading = false
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("Postear en Perfil")
                }

                // Botón para postear en un grupo
                Button(
                    onClick = { showGroupDropdown = true },
                    enabled = !isLoading && groups.isNotEmpty()
                ) {
                    Text("Postear en Grupo")
                }

                // Botón para postear en un evento
                Button(
                    onClick = { showEventInput = true },
                    enabled = !isLoading
                ) {
                    Text("Postear en Evento")
                }
            }

            // Menú desplegable para seleccionar grupo
            if (showGroupDropdown && groups.isNotEmpty()) {
                DropdownMenu(
                    expanded = showGroupDropdown,
                    onDismissRequest = { showGroupDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroup = group
                                showGroupDropdown = false
                                coroutineScope.launch {
                                    isLoading = true
                                    createPost(
                                        content = content,
                                        photoUri = photoUri,
                                        groupId = group.groupId,
                                        eventId = null,
                                        dataService = dataService,
                                        authRepository = authRepository,
                                        onPostCreated = onPostCreated
                                    )
                                    isLoading = false
                                }
                            }
                        )
                    }
                }
            }

            // Campo para ingresar el eventId
            if (showEventInput) {
                AlertDialog(
                    onDismissRequest = { showEventInput = false },
                    title = { Text("Postear en Evento") },
                    text = {
                        Column {
                            Text("Ingresa el ID del evento:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = eventId,
                                onValueChange = { eventId = it },
                                label = { Text("ID del Evento") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (eventId.isNotBlank()) {
                                    coroutineScope.launch {
                                        isLoading = true
                                        createPost(
                                            content = content,
                                            photoUri = photoUri,
                                            groupId = null,
                                            eventId = eventId,
                                            dataService = dataService,
                                            authRepository = authRepository,
                                            onPostCreated = onPostCreated
                                        )
                                        isLoading = false
                                    }
                                    showEventInput = false
                                }
                            },
                            enabled = eventId.isNotBlank()
                        ) {
                            Text("Postear")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showEventInput = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

suspend fun createPost(
    content: String,
    photoUri: Uri?,
    groupId: String?,
    eventId: String?,
    dataService: DataService,
    authRepository: AuthRepository,
    onPostCreated: () -> Unit
) {
    val userId = authRepository.getCurrentUser()?.uid ?: "testUser"

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val now = sdf.format(Date())
    val expiration = sdf.format(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))

    val newPost = Post(
        postId = "",
        userId = userId,
        content = content,
        createdAt = now,
        expirationTime = expiration,
        visibility = if (groupId != null || eventId != null) "private" else "public",
        groupId = groupId,
        eventId = eventId,
        likes = 0,
        imageUrl = photoUri.toString()
    )

    dataService.createPost(newPost)
    onPostCreated()
}