package com.ercoding.foodify.domain

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProteinEntry(
    val id: String = UUID.randomUUID().toString(),
    val meal: String,
    val proteinAmount: Int,
    val emoji: String,
    val createdAt: Long = System.currentTimeMillis()
)