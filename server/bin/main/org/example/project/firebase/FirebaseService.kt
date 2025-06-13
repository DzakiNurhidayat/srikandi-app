package org.example.project.firebase

class FirebaseService(
    private val firebaseRepository: FirebaseRepository
) {

    suspend fun saveToken(userId: String, token: String) {
        try {
            firebaseRepository.saveToken(userId, token)
        } catch (e: Exception) {
            throw RuntimeException("Failed to save token for $userId", e)
        }
    }

    suspend fun getToken(userId: String): String {
        try {
            return firebaseRepository.getToken(userId)
                ?: throw NoSuchElementException("No token found for $userId")
        } catch (e: Exception) {
            throw RuntimeException("Failed to retrieve token for $userId", e)
        }
    }
}
