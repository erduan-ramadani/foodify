package com.eddiapps.foodify.data.remote.anthropic

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val content: List<ResponseContent>
)

@Serializable
data class ResponseContent(
    val type: String,
    val text: String? = null
)