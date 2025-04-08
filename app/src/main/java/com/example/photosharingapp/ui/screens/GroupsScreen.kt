// ui/screens/GroupsScreen.kt
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
import com.example.photosharingapp.model.Group
import com.example.photosharingapp.ui.components.GroupItem
import kotlinx.coroutines.launch

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    dataService: DataService,
    userId: String
) {
    val coroutineScope = rememberCoroutineScope()
    var groups by remember { mutableStateOf(listOf<Group>()) }

    // Cargar grupos al iniciar
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            groups = dataService.getUserGroups(userId)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Título
        Text(
            "Mis Grupos",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        )

        // Lista de grupos
        if (groups.isEmpty()) {
            Text(
                "No tienes grupos",
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
                items(groups) { group ->
                    GroupItem(group.name)
                }
            }
        }
    }
}