package com.example.photosharingapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PhotoDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoDetailScreen()
        }
    }
}

@Composable
fun PhotoDetailScreen() {
    val friendsAndGroups = listOf(
        "Amigo: Juan Pérez",
        "Grupo: Amigos de Viaje",
        "Amigo: María Gómez",
        "Grupo: Fotógrafos Aficionados",
        "Amigo: Carlos López",
        "Grupo: Familia"
    )

    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Foto tomada con éxito", Toast.LENGTH_SHORT).show()
            Log.d("PhotoDetail", "Camera result: Success")
        } else {
            Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
            Log.d("PhotoDetail", "Camera result: Failed")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Toast.makeText(context, "Permiso: $isGranted", Toast.LENGTH_SHORT).show()
        Log.d("PhotoDetail", "Permission granted: $isGranted")
        if (isGranted) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val canResolve = takePictureIntent.resolveActivity(context.packageManager) != null
            Toast.makeText(context, "Puede abrir cámara: $canResolve", Toast.LENGTH_SHORT).show()
            Log.d("PhotoDetail", "Can resolve camera intent: $canResolve")
            if (canResolve) {
                Toast.makeText(context, "Lanzando cámara", Toast.LENGTH_SHORT).show()
                Log.d("PhotoDetail", "Launching camera intent")
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(context, "No se encontró una app de cámara", Toast.LENGTH_SHORT).show()
                Log.d("PhotoDetail", "No camera app found")
            }
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            Log.d("PhotoDetail", "Permission denied")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GreetingSection()
        FriendsAndGroupsFeed(friendsAndGroups = friendsAndGroups, modifier = Modifier.weight(1f))
        TakePhotoButton(onClick = {
            Toast.makeText(context, "Solicitando permiso de cámara", Toast.LENGTH_SHORT).show()
            Log.d("PhotoDetail", "Requesting camera permission")
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        })
    }
}

@Composable
fun GreetingSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Hola, Bienvenido a PhotoDetail!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Explora las fotos de tus amigos y grupos",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun FriendsAndGroupsFeed(friendsAndGroups: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(friendsAndGroups) { item ->
            FeedItem(item)
        }
    }
}

@Composable
fun FeedItem(item: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* Acción al hacer clic */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TakePhotoButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Tomar una Foto",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}