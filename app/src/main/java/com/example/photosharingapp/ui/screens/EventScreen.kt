package com.example.photosharingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photosharingapp.data.DataService
import com.example.photosharingapp.model.Event
import kotlinx.coroutines.launch

@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    dataService: DataService,
    userId: String
) {
    val coroutineScope = rememberCoroutineScope()
    var events by remember { mutableStateOf(listOf<Event>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar eventos desde Firebase
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            events = dataService.getEvents().filter { event ->
                event.participants.contains(userId)
            }
            isLoading = false
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Título
        Text(
            "Mis Eventos",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        )

        // Mostrar un indicador de carga mientras se obtienen los datos
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else if (events.isEmpty()) {
            Text(
                "No tienes eventos",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(events) { event ->
                    EventItem(event = event)
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event) {
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
                text = event.name,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicio: ${event.startTime}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Text(
                text = "Ubicación: ${event.location}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )
            event.endTime?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fin: $it",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }
        }
    }
}