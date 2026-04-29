package com.ercoding.foodify.di

import android.os.Build
import androidx.annotation.RequiresApi
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

@RequiresApi(Build.VERSION_CODES.O)
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule, viewModelModule)
    }
}