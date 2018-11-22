package com.example.darfe.galleryapp

import android.app.Application
import com.example.darfe.galleryapp.di.appModule
import com.facebook.drawee.backends.pipeline.Fresco
import org.koin.android.ext.android.startKoin

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        startKoin(this, listOf(appModule))
    }

}