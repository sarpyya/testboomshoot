package com.example.photosharingapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.photosharingapp.data.FirebaseDataService
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.model.Comment
import com.example.photosharingapp.model.Post
import com.example.photosharingapp.ui.components.PostItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    dataService: FirebaseDataService,
    userId: String,
    modifier: Modifier = Modifier,
    authRepository: AuthRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var commentsByPost by remember { mutableStateOf<Map<String, List<Comment>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Verificar usuario autenticado
    val currentUser by remember { derivedStateOf { FirebaseAuth.getInstance().currentUser } }

    suspend fun refreshPosts() {
        try {
            isLoading = true
            // Filtrar posts para mostrar solo los del usuario
            val fetchedPosts = dataService.getPosts().filter { it.userId == userId }
            posts = fetchedPosts
            val commentsMap = mutableMapOf<String, List<Comment>>()
            fetchedPosts.forEach { post ->
                commentsMap[post.postId] = dataService.getComments(post.postId)
            }
            commentsByPost = commentsMap
            Log.d("MainScreen", "Refreshed ${posts.size} posts for user $userId")
        } catch (e: Exception) {
            Log.e("MainScreen", "Error refreshing posts: ${e.message}", e)
            errorMessage = "Error al cargar tus posts: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (currentUser != null && userId.isNotEmpty()) {
            refreshPosts()
        } else {
            isLoading = false
            errorMessage = "Por favor, inicia sesión para ver tus posts"
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                currentUser == null -> {
                    Text(
                        text = "Inicia sesión para ver tus posts",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                posts.isEmpty() -> {
                    Text(
                        text = "No tienes posts aún",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(posts, key = { it.postId }) { post ->
                            PostItem(
                                post = post,
                                currentUserId = userId,
                                comments = commentsByPost[post.postId] ?: emptyList(),
                                onLikeClicked = { postId ->
                                    coroutineScope.launch {
                                        val newLikes = dataService.toggleLike(postId, userId)
                                        posts = posts.map {
                                            if (it.postId == postId) it.copy(likes = newLikes as List<String>) else it
                                        }
                                    }
                                },
                                onCommentSent = { postId, comment ->
                                    coroutineScope.launch {
                                        dataService.addComment(postId, comment)
                                        commentsByPost = commentsByPost + (postId to dataService.getComments(postId))
                                    }
                                },
                                onDeleteClicked = { postId ->
                                    coroutineScope.launch {
                                        dataService.deletePost(postId)
                                        posts = posts.filter { it.postId != postId }
                                        commentsByPost = commentsByPost - postId
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}