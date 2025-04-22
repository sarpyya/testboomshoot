package com.example.photosharingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photosharingapp.R
import com.example.photosharingapp.data.FirebaseDataService
import com.example.photosharingapp.model.Event
import com.example.photosharingapp.ui.components.CreateEventDialog
import com.example.photosharingapp.ui.components.EventCard
import com.example.photosharingapp.ui.viewmodels.EventsViewModel
import com.example.photosharingapp.ui.viewmodels.EventsViewModelFactory

@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    dataService: FirebaseDataService,
    userId: String,
    onEventCameraClicked: (String) -> Unit,
    onEventClicked: (String) -> Unit
) {
    val viewModel: EventsViewModel = viewModel(
        factory = EventsViewModelFactory(dataService, userId)
    )

    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var joinEventId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { showCreateDialog = true }) {
                Text(stringResource(id = R.string.create_event))
            }
            OutlinedTextField(
                value = joinEventId,
                onValueChange = { joinEventId = it },
                label = { Text(stringResource(id = R.string.join_event)) },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            Button(onClick = {
                if (joinEventId.isNotBlank()) {
                    viewModel.joinEvent(joinEventId)
                }
            }) {
                Text(stringResource(id = R.string.join))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onDeleteClick = { viewModel.deleteEvent(event.eventId) },
                        onEventClick = { onEventClicked(event.eventId) },
                        onCameraClick = { onEventCameraClicked(event.eventId) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showCreateDialog) {
            CreateEventDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    val now = System.currentTimeMillis()
                    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
                    val currentTime = formatter.format(java.util.Date(now))

                    val newEvent = Event(
                        eventId = name.lowercase().replace(" ", "_"),
                        name = name,
                        description = "Descripción por defecto",
                        creatorId = userId,
                        createdAt = currentTime,
                        startTime = currentTime,
                        endTime = Event.calculateEndTime(24), // 24 horas por defecto
                        location = "Ubicación por defecto",
                        participants = listOf(userId),
                        visibility = "public",
                        groupId = null,
                        eventPicture = null,
                        photos = emptyList()
                    )

                    viewModel.createEvent(newEvent)
                    showCreateDialog = false
                }

            )
        }
    }
}
