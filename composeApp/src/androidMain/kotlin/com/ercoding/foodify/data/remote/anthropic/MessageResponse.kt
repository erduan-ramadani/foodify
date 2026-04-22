package com.ercoding.foodify.data.remote.anthropic

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val content: List<ContentBlock> = emptyList()
)

@Serializable
data class ContentBlock(
    val type: String,
    val text: String? = null
)