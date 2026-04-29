package com.ercoding.foodify.presentation

import android.app.Application
import com.ercoding.foodify.di.initKoin
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