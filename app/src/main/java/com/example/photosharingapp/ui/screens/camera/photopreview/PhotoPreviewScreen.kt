package com.example.photosharingapp.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation
import com.example.photosharingapp.data.FirebaseDataService
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.model.Group
import com.example.photosharingapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Transformación para brillo
class BrightnessTransformation(private val brightness: Float) : Transformation {
    override val cacheKey: String = "brightness_$brightness"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val matrix = ColorMatrix().apply {
            setScale(1f + brightness, 1f + brightness, 1f + brightness, 1f)
        }
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }
        val output = createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawBitmap(input, 0f, 0f, paint)
        return output
    }
}

// Crear un post
private suspend fun createPost(
    content: String,
    photoUri: Uri,
    groupId: String?,
    eventId: String?,
    dataService: FirebaseDataService,
    onPostCreated: () -> Unit
) {
    try {
        val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("Usuario no autenticado")
        val userId = user.uid
        Log.d("PhotoPreviewScreen", "Creando post para userId: $userId")

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val now = sdf.format(Date())
        val expiration = sdf.format(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))

        // Subir foto
        Log.d("PhotoPreviewScreen", "Subiendo foto: $photoUri")
        val photoUrl = dataService.uploadPhoto(photoUri, userId).getOrThrow()

        // Crear post
        val newPost = Post(
            postId = UUID.randomUUID().toString(),
            userId = userId,
            content = content,
            createdAt = now,
            expirationTime = expiration,
            visibility = if (groupId != null || eventId != null) "private" else "public",
            groupId = groupId,
            eventId = eventId,
            photoUrl = photoUrl,
            timestamp = System.currentTimeMillis(),
            likes = emptyList(),
            likesCount = 0
        )

        // Guardar en Firestore
        Log.d("PhotoPreviewScreen", "Guardando post en Firestore")
        dataService.createPost(newPost)

        // Añadir foto a evento si aplica
        if (eventId != null) {
            Log.d("PhotoPreviewScreen", "Añadiendo foto a evento: $eventId")
            dataService.addPhotoToEvent(eventId, photoUrl)
        }

        onPostCreated()
    } catch (e: Exception) {
        Log.e("PhotoPreviewScreen", "Error en createPost: ${e.message}", e)
        throw Exception("Error al crear el post: ${e.message}", e)
    }
}

@Composable
fun PhotoPreviewScreen(
    photoUri: Uri?,
    onPostCreated: () -> Unit,
    dataService: FirebaseDataService,
    navController: NavController,
    eventId: String? = null,
    authRepository: AuthRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var content by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var groupsLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var brightness by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar grupos
    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var showGroupOptions by remember { mutableStateOf(false) }

    // Verificar autenticación y cargar grupos
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            errorMessage = "No hay usuario autenticado."
            navController.popBackStack()
        } else {
            try {
                val userId = currentUser.uid
                Log.d("PhotoPreviewScreen", "Cargando grupos para userId: $userId")
                groups = dataService.getUserGroups(userId)
                Log.d("PhotoPreviewScreen", "Grupos cargados: ${groups.size} - ${groups.map { it.name }}")
                if (groups.isEmpty()) {
                    Log.w("PhotoPreviewScreen", "No se encontraron grupos para userId: $userId")
                }
            } catch (e: Exception) {
                Log.e("PhotoPreviewScreen", "Error cargando grupos: ${e.message}", e)
                errorMessage = "Error al cargar grupos: ${e.message}"
            } finally {
                groupsLoading = false
            }
        }
    }

    // Mostrar errores
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    // Animación para botón
    var publishClicked by remember { mutableStateOf(false) }
    val publishScale by animateFloatAsState(if (publishClicked) 0.9f else 1f)

    // Función para manejar la publicación
    fun handlePublish() {
        if (!isLoading) {
            publishClicked = true
            coroutineScope.launch {
                isLoading = true
                try {
                    if (photoUri == null) {
                        throw IllegalStateException("No se proporcionó una imagen")
                    }
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        throw Exception("Usuario no autenticado")
                    }
                    Log.d("PhotoPreviewScreen", "Intentando publicar con photoUri: $photoUri")
                    createPost(
                        content = content,
                        photoUri = photoUri,
                        groupId = selectedGroupId,
                        eventId = eventId,
                        dataService = dataService,
                        onPostCreated = {
                            onPostCreated()
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.e("PhotoPreviewScreen", "Error al publicar: ${e.message}", e)
                    errorMessage = "Error al publicar: ${e.message}"
                } finally {
                    isLoading = false
                    publishClicked = false
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Vista previa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 3f)
                                offset += pan
                            }
                        }
                ) {
                    if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(photoUri)
                                    .size(Size(512, 512))
                                    .transformations(BrightnessTransformation(brightness))
                                    .build()
                            ),
                            contentDescription = "Foto tomada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 350.dp)
                                .scale(scale)
                                .offset(x = offset.x.dp, y = offset.y.dp),
                            contentScale = if (scale > 1f) ContentScale.Fit else ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "No se pudo cargar la imagen",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                OutlinedTextField(
                    value = content,
                    onValueChange = { if (it.length <= 280) content = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
                Text(
                    text = "${content.length}/280",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Opciones de compartir
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = showGroupOptions,
                        onClick = { showGroupOptions = !showGroupOptions },
                        label = { Text("Grupos") },
                        leadingIcon = {
                            if (showGroupOptions) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    )
                    FilterChip(
                        selected = eventId != null,
                        onClick = {},
                        label = { Text("Eventos") },
                        leadingIcon = {
                            if (eventId != null) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        },
                        enabled = false
                    )
                }

                // Selección de grupos
                if (showGroupOptions) {
                    when {
                        groupsLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        groups.isNotEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Text("Seleccionar grupo", fontSize = 14.sp)
                                groups.take(3).forEach { group ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedGroupId == group.groupId,
                                            onClick = { selectedGroupId = group.groupId }
                                        )
                                        Text(group.name, modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                                if (groups.size > 3) {
                                    TextButton(onClick = { /* Navegar a lista completa */ }) {
                                        Text("Ver más grupos")
                                    }
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = "No se encontraron grupos. Crea uno primero.",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de publicación
                Button(
                    onClick = { handlePublish() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .scale(publishScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Publicar", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancelar
                TextButton(
                    onClick = { navController.popBackStack() },
                    enabled = !isLoading,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancelar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelar")
                }

                if (isLoading && !groupsLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}