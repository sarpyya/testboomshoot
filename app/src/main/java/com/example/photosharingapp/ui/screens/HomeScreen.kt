// ui/screens/HomeScreen.kt
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
import com.example.photosharingapp.model.Post
import com.example.photosharingapp.ui.components.GroupItem
import com.example.photosharingapp.ui.components.PostItem
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    dataService: DataService,
    userId: String
) {
    val coroutineScope = rememberCoroutineScope()
    var posts by remember { mutableStateOf(listOf<Post>()) }
    var groups by remember { mutableStateOf(listOf<String>()) }

    // Cargar datos de manera asíncrona
    LaunchedEffect(Unit) {
        coroutineScope.launch {
           // posts = dataService.getPosts()
           // groups = dataService.getUserGroups(userId).map { it.name }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            "Home",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        )

        if (posts.isEmpty() && groups.isEmpty()) {
            Text(
                "No hay contenido para mostrar",
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
                item {
                    Text(
                        "Grupos",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    if (groups.isEmpty()) {
                        Text(
                            "No estás en ningún grupo",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        groups.forEach { groupName ->
                            GroupItem(groupName)
                        }
                    }
                }
                item {
                    Text(
                        "Posts",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                if (posts.isEmpty()) {
                    item {
                        Text(
                            "No hay posts disponibles",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(posts) { post ->
                        PostItem(post)
                    }
                }
            }
        }
    }
}