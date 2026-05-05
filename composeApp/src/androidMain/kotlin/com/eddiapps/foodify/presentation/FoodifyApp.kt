package com.eddiapps.foodify.presentation

import android.app.Application
import com.eddiapps.foodify.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FoodifyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FoodifyApp)
            androidLogger()
        }
    }
}