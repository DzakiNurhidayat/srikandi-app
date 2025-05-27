package org.example.project.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getUserDocument(uid: String): DocumentSnapshot? {
        return try {
            firestore.collection("users").document(uid).get().await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPendingUserByEmail(email: String): DocumentSnapshot? {
        return try {
            val querySnapshot = firestore.collection("users_pending")
                .whereEqualTo("email", email.trim().lowercase())
                .limit(1)
                .get()
                .await()
            querySnapshot.documents.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getRoleDefinitions(): List<DocumentSnapshot> {
        return try {
            firestore.collection("roles").get().await().documents
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveNewUser(uid: String, userData: Map<String, Any?>) {
        firestore.collection("users").document(uid).set(userData, SetOptions.merge()).await()
    }

    suspend fun saveDeviceToken(userUid: String, deviceUid: String, deviceData: Map<String, Any>) {
        firestore.collection("users").document(userUid)
            .collection("devices").document(deviceUid)
            .set(deviceData, SetOptions.merge())
            .await()
    }

    suspend fun updateUserActiveRole(uid: String, role: String) {
        firestore.collection("users").document(uid).update("activeRole", role).await()
    }

    suspend fun clearUserActiveRole(uid: String, deviceId: String) {
        try {
            val userRef = firestore.collection("users").document(uid)
            val deviceRef = userRef.collection("devices").document(deviceId)

            val batch = firestore.batch()
            batch.update(userRef, "activeRole", null)
            batch.update(deviceRef, "isActive", false)
            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun updateUserProfileField(uid: String, field: String, value: Any?) {
        try {
            firestore.collection("users").document(uid)
                .update(field, value)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}