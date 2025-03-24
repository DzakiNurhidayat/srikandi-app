package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
abstract class Entity(open val id: Int)

