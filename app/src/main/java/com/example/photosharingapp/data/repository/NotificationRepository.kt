package com.example.photosharingapp.data.repository

import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

// ... other imports

class NotificationRepository : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token refresh logic, you might call getAuthTokenWithRetry here if needed
        // but typically the token from onNewToken is sufficient for registering
        // with your backend.  FIS token is generally for internal Firebase operations.
    }

    // ... other methods

    // Place the function inside your service

    suspend fun getAuthTokenWithRetry(maxRetries: Int = 3) {
        var retries = 0
        while (retries < maxRetries) {
            try {
                val token = FirebaseInstallations.getInstance().getToken(false).await()
                // Use the token
                println("Successfully obtained FIS auth token: $token")
                return
            } catch (e: Exception) {
                if (e.message?.contains("Firebase Installations Service is unavailable") == true) {
                    retries++
                    println("Failed to get FIS auth token (attempt $retries/$maxRetries): ${e.message}")
                    if (retries < maxRetries) {
                        delay(2000) // Wait for 2 seconds before retrying
                    }
                } else {
                    // Handle other exceptions (e.g., configuration issues)
                    println("Error getting FIS auth token: ${e.message}")
                    return
                }
            }
        }
        println("Failed to get FIS auth token after $maxRetries attempts.")
    }
}