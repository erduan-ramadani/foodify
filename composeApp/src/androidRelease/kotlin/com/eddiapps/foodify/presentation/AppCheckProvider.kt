package com.eddiapps.foodify.presentation

import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

fun appCheckProviderFactory(): AppCheckProviderFactory {
    return PlayIntegrityAppCheckProviderFactory.getInstance()
}