package com.example.photosharingapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) {
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            _currentUser.value = result.user // Actualiza el estado del usuario
            result.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            _currentUser.value = result.user // Actualiza el estado del usuario
            result.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            _currentUser.value = result.user // Actualiza el estado del usuario
            result.user
        } catch (e: Exception) {
            null
        }
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun getCurrentUser(): FirebaseUser? {
        val user = auth.currentUser
        Log.d("AuthRepository", "Current user: $user")
        return user
    }

    suspend fun signInAnonymously(): FirebaseUser? {
        return try {
            val result = auth.signInAnonymously().await()
            val user = result.user
            Log.d("AuthRepository", "Anonymous sign-in successful: ${user?.uid}")
            user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Anonymous sign-in failed: ${e.message}", e)
            null
        }
    }
    fun signOut() {
        firebaseAuth.signOut()
        _currentUser.value = null // Asegúrate de limpiar el estado de usuario al cerrar sesión
    }


}
