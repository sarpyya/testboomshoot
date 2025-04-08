package com.example.photosharingapp.model

data class Post(
    val postId: String = "",
    val userId: String = "",
    val content: String = "",
    val createdAt: String = "",
    val expirationTime: String = "",
    val visibility: String = "",
    val groupId: String? = null,
    val eventId: String? = null,
    val likes: Int = 0,
    val imageUrl: String? = null
)
