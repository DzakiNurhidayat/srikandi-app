package org.example.project.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.example.project.data.entities.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserById(uid: String): Flow<UserEntity?>

    @Query("SELECT fcmToken FROM users WHERE uid = :userId LIMIT 1")
    suspend fun getUserToken(userId: Int): String?

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserById(uid: String)
}
