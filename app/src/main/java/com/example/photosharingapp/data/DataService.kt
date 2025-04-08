package com.example.photosharingapp.data

import com.example.photosharingapp.model.*

interface DataService {
    // Métodos de migración a Firestore
    suspend fun migrateEventsToFirestore()
    suspend fun migrateGroupsToFirestore()
    suspend fun migrateRelationshipsToFirestore()
    suspend fun migrateUsersToFirestore()
    suspend fun migratePostsToFirestore()

    // Métodos para obtener datos de Firestore
    suspend fun getEvents(): List<Event>
    suspend fun getGroups(): List<Group>
    suspend fun getRelationships(): List<Relationship>
    suspend fun getUsers(): List<Users>
    suspend fun getUser(userId: String): Users?
    suspend fun getUserById(userId: String): Users?
    suspend fun getPosts(): List<Post>
    suspend fun getUserGroups(userId: String): List<Group>

    // Métodos para agregar o crear datos en Firestore
    suspend fun createEvent(event: Event)
    suspend fun addGroup(group: Group)
    suspend fun addRelationship(relationship: Relationship)
    suspend fun addUser(user: Users)
    suspend fun createPost(post: Post)

    // Métodos para acceder a los datos locales (pueden seguir siendo mock o desde la base local)
    fun getLocalEvents(): List<Event>
    fun getLocalGroups(): List<Group>
    fun getLocalRelationships(): List<Relationship>
    fun getLocalUsers(): List<Users>
    fun getLocalPosts(): List<Post>
}
