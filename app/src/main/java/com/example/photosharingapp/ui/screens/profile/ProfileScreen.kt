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
import com.example.photosharingapp.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    val postCount by viewModel.postCount.collectAsState()
    val groupCount by viewModel.groupCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Perfil", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (user == null) {
            Text("Cargando perfil...", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
        } else {
            user?.photoUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            } ?: Surface(
                modifier = Modifier.size(120.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = user?.displayName?.firstOrNull()?.toString() ?: "U",
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(user?.displayName ?: "Usuario", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(user?.email ?: "Sin correo", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("Posts", postCount.toString())
                StatItem("Grupos", groupCount.toString())
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