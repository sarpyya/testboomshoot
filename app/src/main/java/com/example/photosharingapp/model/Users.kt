package com.example.photosharingapp.model

data class Users(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val profilePicture: String? = null,
    val createdAt: String = "",
    val groups: List<String> = emptyList()
)
