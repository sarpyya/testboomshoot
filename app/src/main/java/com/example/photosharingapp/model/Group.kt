package com.example.photosharingapp.model

data class Group(
    val groupId: String = "",
    val name: String = "",
    val description: String = "",
    val creatorId: String = "",
    val createdAt: String = "",
    val members: List<String> = emptyList(),
    val visibility: String = "",
    val groupPicture: String? = null
)
