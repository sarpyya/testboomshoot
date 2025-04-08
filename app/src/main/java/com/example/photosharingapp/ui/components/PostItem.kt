// ui/components/PostItem.kt
package com.example.photosharingapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.photosharingapp.model.Post

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Creado: ${post.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Expira: ${post.expirationTime}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Likes: ${post.likes}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )
            post.imageUrl?.let { url ->
                Spacer(modifier = Modifier.height(8.dp))
                // Mostrar la imagen si existe
                // Nota: Necesitarás la librería Coil para cargar imágenes
                 Image(
                     painter = rememberAsyncImagePainter(model = url),
                     contentDescription = "Imagen del post",
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(200.dp)
                 )
            }
        }
    }
}