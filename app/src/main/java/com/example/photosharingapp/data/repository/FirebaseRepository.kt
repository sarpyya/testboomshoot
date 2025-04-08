package com.example.photosharingapp.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

data class Group(
    val creationTime: Long = 0,
    val id: String = "",
    val isEvent: Boolean = false,
    val members: List<String> = emptyList(), // Asegúrate de que sea una lista
    val name: String = ""
)

class FirebaseRepository(
    private val authRepository: AuthRepository
) {
    private val groupsCollection: CollectionReference = Firebase.firestore.collection("groups")

    suspend fun getUserGroups(uid: String): List<Group> {
        val userId = authRepository.getCurrentUser()?.uid ?: run {
            Log.e("FirebaseRepository", "No authenticated user")
            return emptyList()
        }

        return try {
            Log.d("FirebaseRepository", "Fetching groups for userId: $userId")
            val snapshot = groupsCollection
                .whereArrayContains("members", userId)
                .get()
                .await()

            val groups = snapshot.toObjects(Group::class.java)
            Log.d("FirebaseRepository", "Found ${snapshot.documents.size} documents: $groups")
            groups
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Firestore error: ${e.message}", e)
            if (e is CancellationException) throw e
            emptyList()
        }
    }
}