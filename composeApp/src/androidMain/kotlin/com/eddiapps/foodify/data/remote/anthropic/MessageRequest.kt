package com.eddiapps.foodify.data.remote.anthropic


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class MessageRequest(
    val model: String,
    val max_tokens: Int,
    val temperature: Double,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: List<ContentBlock>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class ContentBlock {

    @Serializable
    @SerialName("text")
    data class Text(val text: String) : ContentBlock()

    @Serializable
    @SerialName("image")
    data class Image(val source: ImageSource) : ContentBlock()
}

@Serializable
data class ImageSource(
    val type: String = "base64",
    val media_type: String = "image/jpeg",
    val data: String
)
