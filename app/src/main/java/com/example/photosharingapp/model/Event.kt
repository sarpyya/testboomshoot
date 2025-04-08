package com.example.photosharingapp.model

data class Event(
    val eventId: String = "",
    val name: String = "",
    val description: String = "",
    val creatorId: String = "",
    val createdAt: String = "",
    val startTime: String = "",
    val endTime: String? = null,
    val location: String = "",
    val participants: List<String> = emptyList(),
    val visibility: String = "",
    val groupId: String? = null,
    val eventPicture: String? = null
)
