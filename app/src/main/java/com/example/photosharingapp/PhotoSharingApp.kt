package com.example.photosharingapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.example.photosharingapp.di.appModule
import com.google.firebase.BuildConfig

class PhotoSharingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d("PhotoSharingApp", "App Check configurado con proveedor de depuración")
        } else {
            // Para producción, configura Play Integrity si aplica
            // firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
            Log.d("PhotoSharingApp", "App Check no configurado en modo producción")
        }

        startKoin {
            androidContext(this@PhotoSharingApp)
            modules(appModule)
        }
    }
}