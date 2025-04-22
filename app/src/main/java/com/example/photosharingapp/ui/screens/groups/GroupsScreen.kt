package com.example.photosharingapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photosharingapp.data.FirebaseDataService
import com.example.photosharingapp.model.Group
import com.example.photosharingapp.ui.components.GroupItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    dataService: FirebaseDataService,
    userId: String
) {
    val coroutineScope = rememberCoroutineScope()
    var groups by remember { mutableStateOf(listOf<Group>()) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var groupNameInput by remember { mutableStateOf("") }
    var groupDescriptionInput by remember { mutableStateOf("") }
    var groupVisibilityInput by remember { mutableStateOf("public") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load groups on start
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                groups = dataService.getUserGroups(userId)
            } catch (e: Exception) {
                Log.e("GroupsScreen", "Error loading groups: ${e.message}", e)
                errorMessage = "Error al cargar los grupos: ${e.message}"
            }
        }
    }

    // Show error in Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateGroupDialog = true },
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear grupo"
                    )
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Title
            Text(
                text = "Mis Grupos",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start)
            )

            // Group list
            if (groups.isEmpty()) {
                Text(
                    text = "No tienes grupos",
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
                    items(groups, key = { it.groupId }) { group ->
                        GroupItem(
                            group = group,
                            onClick = {
                                Log.d("GroupsScreen", "Clicked group: ${group.groupId}")
                            }
                        )
                    }
                }
            }
        }

        // Create group dialog
        if (showCreateGroupDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCreateGroupDialog = false
                    groupNameInput = ""
                    groupDescriptionInput = ""
                    groupVisibilityInput = "public"
                },
                title = { Text("Crear nuevo grupo") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = groupNameInput,
                            onValueChange = { groupNameInput = it },
                            label = { Text("Nombre del grupo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = groupDescriptionInput,
                            onValueChange = { groupDescriptionInput = it },
                            label = { Text("Descripción (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Text("Visibilidad")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("public", "private").forEach { visibility ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = groupVisibilityInput == visibility,
                                        onClick = { groupVisibilityInput = visibility }
                                    )
                                    Text(visibility.replaceFirstChar { it.uppercase() })
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    if (groupNameInput.isNotBlank()) {
                                        val timestamp = SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                            Locale.getDefault()
                                        ).apply {
                                            timeZone = TimeZone.getTimeZone("UTC")
                                        }.format(Date())
                                        val newGroup = Group(
                                            groupId = "",
                                            name = groupNameInput,
                                            description = groupDescriptionInput,
                                            creatorId = userId,
                                            createdAt = timestamp,
                                            members = listOf(userId),
                                            visibility = groupVisibilityInput,
                                            groupPicture = null
                                        )
                                        dataService.addGroup(newGroup)
                                        groups = groups + newGroup.copy(
                                            groupId = UUID.randomUUID().toString()
                                        )
                                        showCreateGroupDialog = false
                                        groupNameInput = ""
                                        groupDescriptionInput = ""
                                        groupVisibilityInput = "public"
                                    } else {
                                        errorMessage = "El nombre del grupo no puede estar vacío"
                                    }
                                } catch (e: Exception) {
                                    Log.e("GroupsScreen", "Error creating group: ${e.message}", e)
                                    errorMessage = "Error al crear el grupo: ${e.message}"
                                }
                            }
                        },
                        enabled = groupNameInput.isNotBlank()
                    ) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCreateGroupDialog = false
                            groupNameInput = ""
                            groupDescriptionInput = ""
                            groupVisibilityInput = "public"
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}