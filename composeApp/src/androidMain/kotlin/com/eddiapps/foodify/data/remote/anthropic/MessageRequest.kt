package com.eddiapps.foodify.data.remote.anthropic

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val model: String,
    val max_tokens: Int,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

