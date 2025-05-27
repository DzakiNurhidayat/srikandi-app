package org.example.project.data.model

data class UserProfile(
    val uid: String = "",
    val nama: String = "",
    val nim: String = "",
    val email: String? = null,
    val jurusan: String = "",
    val fotoProfil: String = "",
    val kontak: String = "",
    val alamat: String = "",
    val jenisKelamin: String = "",
    val activeRole: String = "",
)