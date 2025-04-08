package com.example.photosharingapp.data

import android.util.Log
import com.example.photosharingapp.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseDataService : DataService {
    private val firestore = FirebaseFirestore.getInstance()

    // Referencias a las colecciones de Firestore
    private val eventsRef = firestore.collection("events")
    private val groupsRef = firestore.collection("groups")
    private val relationshipsRef = firestore.collection("relationships")
    private val usersRef = firestore.collection("users")
    private val postsRef = firestore.collection("posts")

    // Métodos de migración
    override suspend fun migrateEventsToFirestore() {
        try {
            val localEvents = MockDatabase.events // Datos de MockDatabase
            val existingEvents = getEvents() // Obtiene los eventos de Firestore
            val existingEventIds = existingEvents.map { it.eventId }

            coroutineScope {
                localEvents.forEach { event ->
                    if (event.eventId !in existingEventIds) {
                        // Migrar a Firestore
                        eventsRef.document(event.eventId).set(event).await()
                        Log.d("FirebaseDataService", "Migrated event to Firestore: ${event.name}")
                    } else {
                        Log.d("FirebaseDataService", "Event already exists in Firestore: ${event.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error migrating events to Firestore: ${e.message}", e)
            throw e
        }
    }

    override suspend fun migrateGroupsToFirestore() {
        try {
            val localGroups = MockDatabase.groups // Datos de MockDatabase
            val existingGroups = getGroups() // Obtiene los grupos de Firestore
            val existingGroupIds = existingGroups.map { it.groupId }

            coroutineScope {
                localGroups.forEach { group ->
                    if (group.groupId !in existingGroupIds) {
                        // Migrar a Firestore
                        groupsRef.document(group.groupId).set(group).await()
                        Log.d("FirebaseDataService", "Migrated group to Firestore: ${group.name}")
                    } else {
                        Log.d("FirebaseDataService", "Group already exists in Firestore: ${group.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error migrating groups to Firestore: ${e.message}", e)
            throw e
        }
    }

    override suspend fun migrateRelationshipsToFirestore() {
        try {
            val localRelationships = MockDatabase.relationships // Datos de MockDatabase
            val existingRelationships = getRelationships() // Obtiene las relaciones de Firestore
            val existingRelationshipIds = existingRelationships.map { it.relationshipId }

            coroutineScope {
                localRelationships.forEach { relationship ->
                    if (relationship.relationshipId !in existingRelationshipIds) {
                        // Migrar a Firestore
                        relationshipsRef.document(relationship.relationshipId).set(relationship).await()
                        Log.d("FirebaseDataService", "Migrated relationship to Firestore: ${relationship.relationshipId}")
                    } else {
                        Log.d("FirebaseDataService", "Relationship already exists in Firestore: ${relationship.relationshipId}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error migrating relationships to Firestore: ${e.message}", e)
            throw e
        }
    }

    override suspend fun migrateUsersToFirestore() {
        try {
            val localUsers = MockDatabase.users // Datos de MockDatabase
            val existingUsers = getUsers() // Obtiene los usuarios de Firestore
            val existingUserIds = existingUsers.map { it.userId }

            coroutineScope {
                localUsers.forEach { user ->
                    if (user.userId !in existingUserIds) {
                        // Migrar a Firestore
                        usersRef.document(user.userId).set(user).await()
                        Log.d("FirebaseDataService", "Migrated user to Firestore: ${user.username}")
                    } else {
                        Log.d("FirebaseDataService", "User already exists in Firestore: ${user.username}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error migrating users to Firestore: ${e.message}", e)
            throw e
        }
    }

    override suspend fun migratePostsToFirestore() {
        try {
            val localPosts = MockDatabase.posts // Datos de MockDatabase
            val existingPosts = getPosts() // Obtiene los posts de Firestore
            val existingPostIds = existingPosts.map { it.postId }

            coroutineScope {
                localPosts.forEach { post ->
                    if (post.postId !in existingPostIds) {
                        // Migrar a Firestore
                        postsRef.document(post.postId).set(post).await()
                        Log.d("FirebaseDataService", "Migrated post to Firestore: ${post.content}")
                    } else {
                        Log.d("FirebaseDataService", "Post already exists in Firestore: ${post.postId}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error migrating posts to Firestore: ${e.message}", e)
            throw e
        }
    }

    // Métodos para obtener los datos desde Firestore
    override suspend fun getEvents(): List<Event> {
        try {
            val snapshot = eventsRef.get().await()
            return snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error getting events from Firestore: ${e.message}", e)
            return emptyList() // Devuelve una lista vacía en caso de error
        }
    }

    override suspend fun getGroups(): List<Group> {
        try {
            val snapshot = groupsRef.get().await()
            return snapshot.documents.mapNotNull { it.toObject(Group::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error getting groups from Firestore: ${e.message}", e)
            return emptyList()
        }
    }

    override suspend fun getRelationships(): List<Relationship> {
        try {
            val snapshot = relationshipsRef.get().await()
            return snapshot.documents.mapNotNull { it.toObject(Relationship::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error getting relationships from Firestore: ${e.message}", e)
            return emptyList()
        }
    }

    override suspend fun getUsers(): List<Users> {
        try {
            val snapshot = usersRef.get().await()
            return snapshot.documents.mapNotNull { it.toObject(Users::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error getting users from Firestore: ${e.message}", e)
            return emptyList()
        }
    }

    override suspend fun getUser(userId: String): Users? {
        TODO("Not yet implemented")
    }

    // Implementación de getUserById
    override suspend fun getUserById(userId: String): Users? {
        try {
            val snapshot = usersRef.document(userId).get().await()
            return snapshot.toObject(Users::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error getting user by id $userId from Firestore: ${e.message}", e)
            return null
        }
    }

    override suspend fun getPosts(): List<Post> {
        try {
            val snapshot = postsRef.get().await()
            return snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error getting posts from Firestore: ${e.message}", e)
            return emptyList()
        }
    }

    override suspend fun getUserGroups(userId: String): List<Group> {
        TODO("Not yet implemented")
    }

    // Métodos para agregar a Firestore
    override suspend fun createEvent(event: Event) {
        try {
            eventsRef.document(event.eventId).set(event).await()
            Log.d("FirebaseDataService", "Added event to Firestore: ${event.name}")
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error adding event to Firestore: ${e.message}", e)
        }
    }

    override suspend fun addGroup(group: Group) {
        try {
            groupsRef.document(group.groupId).set(group).await()
            Log.d("FirebaseDataService", "Added group to Firestore: ${group.name}")
        } catch (e: Exception) {
            Log.e("FirebaseDataService", "Error adding group to Firestore: ${e.message}", e)
        }
    }

    override suspend fun addRelationship(relationship: Relationship) {
        TODO("Not yet implemented")
    }

    override suspend fun addUser(user: Users) {
        TODO("Not yet implemented")
    }

    override suspend fun createPost(post: Post) {
        TODO("Not yet implemented")
    }

    override fun getLocalEvents(): List<Event> {
        TODO("Not yet implemented")
    }

    override fun getLocalGroups(): List<Group> {
        TODO("Not yet implemented")
    }

    override fun getLocalRelationships(): List<Relationship> {
        TODO("Not yet implemented")
    }

    override fun getLocalUsers(): List<Users> {
        TODO("Not yet implemented")
    }

    override fun getLocalPosts(): List<Post> {
        TODO("Not yet implemented")
    }
}
