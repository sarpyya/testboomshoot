package com.example.photosharingapp.data

import com.example.photosharingapp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataService : DataService {

    // Se podría reemplazar MockDatabase con una base de datos real (ej. Room)

    override suspend fun getUsers(): List<Users> {
        return MockDatabase.users
    }

    override suspend fun getUser(userId: String): Users? {
        return MockDatabase.users.find { it.userId == userId }
    }

    override suspend fun getUserById(userId: String): Users? {
        return MockDatabase.users.find { it.userId == userId }
    }

    override suspend fun getPosts(): List<Post> {
        return MockDatabase.posts
    }

    override suspend fun createPost(post: Post) {
        MockDatabase.posts.add(post)
    }

    override suspend fun getEvents(): List<Event> {
        return MockDatabase.events
    }

    override suspend fun createEvent(event: Event) {
        MockDatabase.events.add(event)
    }

    override suspend fun getGroups(): List<Group> {
        return MockDatabase.groups
    }

    override suspend fun getRelationships(): List<Relationship> {
        return MockDatabase.relationships
    }

    override suspend fun getUserGroups(userId: String): List<Group> {
        return MockDatabase.groups.filter { group -> userId in group.members }
    }

    override suspend fun addGroup(group: Group) {
        MockDatabase.groups.add(group)
    }

    override suspend fun addRelationship(relationship: Relationship) {
        MockDatabase.relationships.add(relationship)
    }

    override suspend fun addUser(user: Users) {
        MockDatabase.users.add(user)
    }

    // Métodos de migración con nombres corregidos para Firestore
    override suspend fun migrateEventsToFirestore() {
        // Implementar migración si es necesario
    }

    override suspend fun migrateGroupsToFirestore() {
        // Implementar migración si es necesario
    }

    override suspend fun migrateRelationshipsToFirestore() {
        // Implementar migración si es necesario
    }

    override suspend fun migrateUsersToFirestore() {
        // Implementar migración si es necesario
    }

    override suspend fun migratePostsToFirestore() {
        // Implementar migración si es necesario
    }

    // Métodos locales para obtener datos sin hacer llamadas a la red
    override fun getLocalEvents(): List<Event> {
        return MockDatabase.events
    }

    override fun getLocalGroups(): List<Group> {
        return MockDatabase.groups
    }

    override fun getLocalRelationships(): List<Relationship> {
        return MockDatabase.relationships
    }

    override fun getLocalUsers(): List<Users> {
        return MockDatabase.users
    }

    override fun getLocalPosts(): List<Post> {
        return MockDatabase.posts
    }
}
