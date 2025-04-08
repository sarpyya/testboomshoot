// data/MockDatabase.kt
package com.example.photosharingapp.data

import com.example.photosharingapp.model.Event
import com.example.photosharingapp.model.Group
import com.example.photosharingapp.model.Post
import com.example.photosharingapp.model.Relationship
import com.example.photosharingapp.model.Users

object MockDatabase {
    val users = mutableListOf<Users>(
        Users(
            userId = "testUser",
            username = "juanperez",
            email = "juan@example.com",
            profilePicture = "https://example.com/juan.jpg",
            createdAt = "2025-04-07T10:00:00Z",
            groups = listOf("group001")
        ),
        Users(
            userId = "def456",
            username = "anarodriguez",
            email = "ana@example.com",
            profilePicture = "https://example.com/ana.jpg",
            createdAt = "2025-04-07T11:00:00Z",
            groups = listOf("group001")
        )
    )

    val posts = mutableListOf<Post>(
        Post(
            postId = "post001",
            userId = "abc123",
            content = "¡Este post desaparecerá pronto!",
            createdAt = "2025-04-07T12:00:00Z",
            expirationTime = "2025-04-08T12:00:00Z",
            visibility = "public",
            groupId = null,
            eventId = null,
            likes = 3,
            imageUrl = null
        )
    )

    val relationships = mutableListOf<Relationship>(
        Relationship(
            relationshipId = "rel001",
            userId = "abc123",
            targetUserId = "def456",
            status = "accepted",
            createdAt = "2025-04-07T14:00:00Z"
        )
    )

    val groups = mutableListOf<Group>(
        Group(
            groupId = "group001",
            name = "Amigos de Viaje",
            description = "Para planear nuestras aventuras",
            creatorId = "abc123",
            createdAt = "2025-04-07T15:00:00Z",
            members = listOf("abc123", "def456"),
            visibility = "private",
            groupPicture = "https://example.com/group.jpg"
        )
    )

    val events = mutableListOf<Event>(
        Event(
            eventId = "event001",
            name = "Fiesta de Cumpleaños",
            description = "¡Trae tu mejor energía!",
            creatorId = "testUser",
            createdAt = "2025-04-07T15:00:00Z",
            startTime = "2025-04-10T20:00:00Z",
            endTime = "2025-04-11T02:00:00Z",
            location = "Calle Falsa 123",
            participants = listOf("testUser", "abc123"),
            visibility = "private",
            groupId = null,
            eventPicture = "https://example.com/event.jpg"
        ),
        Event(
            eventId = "event002",
            name = "Reunión de Amigos",
            description = "Cena y juegos de mesa",
            creatorId = "abc123",
            createdAt = "2025-04-08T10:00:00Z",
            startTime = "2025-04-12T18:00:00Z",
            endTime = null,
            location = "Av. Siempre Viva 742",
            participants = listOf("testUser"),
            visibility = "private",
            groupId = "group001",
            eventPicture = null
        )
    )
}