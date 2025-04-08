package com.example.photosharingapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.photosharingapp.*
import com.example.photosharingapp.data.repository.AuthRepository
import com.example.photosharingapp.data.repository.FirebaseRepository
import com.example.photosharingapp.data.repository.NotificationRepository
import com.example.photosharingapp.data.repository.PhotoRepository
import com.example.photosharingapp.data.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseMessaging.getInstance() }
    single { FirebaseRepository(get()) }
    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    single { AuthRepository(get()) }
    single { SettingsRepository(get()) }
}
