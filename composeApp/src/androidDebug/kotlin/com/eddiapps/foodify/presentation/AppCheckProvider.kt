package com.eddiapps.foodify.presentation

import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

fun appCheckProviderFactory(): AppCheckProviderFactory {
    return DebugAppCheckProviderFactory.getInstance()
}