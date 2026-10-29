package com.eddiapps.foodify.presentation

import android.app.Application
import com.eddiapps.foodify.BuildConfig
import com.eddiapps.foodify.di.initKoin
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FoodifyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        if (BuildConfig.DEBUG) {
            FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
        } else {
            FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
                )
        }

        initKoin {
            androidContext(this@FoodifyApp)
            androidLogger()
        }
    }
}