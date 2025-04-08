plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "com.example.photosharingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.photosharingapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.play.services.auth)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0-alpha02")
    androidTestImplementation(libs.androidx.espresso.core)

    // ✅ Koin
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
    implementation("io.insert-koin:koin-core:3.5.3")

    // ❌ Hilt (eliminado)
    // implementation(libs.dagger.hilt.android)
    // ksp(libs.hilt.compiler)

    // JUnit Jupiter
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation(libs.com.google.firebase.firebase.auth.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.database.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.google.firebase.storage.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
    // Coil for Compose
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Kotlin 2.1.20
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation(libs.firebase.appcheck.debug)
    // CameraX
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    // Para integrar CameraX con Compose
    implementation("androidx.camera:camera-compose:1.5.0-alpha06")
}
