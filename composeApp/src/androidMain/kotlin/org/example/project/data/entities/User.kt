package org.example.project.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.FieldValue
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val kode: String?,
    val nama: String?,
    val email: String?,
    val photoUrl: String?,
    val activeRole: String?,
    val roles: List<String>?,
    val kontak: String?,
    val jnsKelamin: String?,
    val tglLahir: Date,
    val alamat: String?,
    val lastLogin: Long?,
    val fcmToken: String?
)