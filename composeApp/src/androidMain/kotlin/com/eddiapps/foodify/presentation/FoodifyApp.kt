package com.eddiapps.foodify.presentation

import android.app.Application
import com.eddiapps.foodify.di.initKoin
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FoodifyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        FirebaseAppCheck.getInstance()
            .installAppCheckProviderFactory(appCheckProviderFactory())

        initKoin {
            androidContext(this@FoodifyApp)
            androidLogger()
        }
    }
}