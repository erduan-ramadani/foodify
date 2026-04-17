package com.ercoding.foodify.data.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private var apiKey: String = ""
    val configSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(0)
        .build()

    suspend fun fetchAnthropicApiKey(): String {
        if (apiKey.isBlank()) {
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().await()
            apiKey = remoteConfig.getString("anthropicApiKey")
        }
        val activated = remoteConfig.fetchAndActivate().await()
        println("Activated: $activated")
        apiKey = remoteConfig.getString("anthropicApiKey")
        println("API Key: $apiKey")

        return apiKey
    }
}