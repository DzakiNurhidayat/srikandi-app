package org.example.project.application.models

data class FirebaseUserPrincipal(
    val uid: String,
    val email: String?,
    val name: String?,
)