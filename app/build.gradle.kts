plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Tetap menggunakan Compose
}

android {
    namespace = "com.example.looksy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.looksy"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true // Pertahankan fitur Compose
    }

    @Suppress("UnstableApiUsage")
    androidResources {
        noCompress("tflite")
    }
}

// --- Dependensi Inti Android & Compose (Direvisi) ---
dependencies {
    // 1. Dapatkan semua versi Compose yang kompatibel dari BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.04.00") // Versi BOM stabil
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // 2. Dependensi Compose Inti yang diperlukan
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // --- Aktivitas & Lifecycle ---
    // Pastikan versi ini ada dan tidak tumpang tindih
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") // Kunci untuk lifecycle
    implementation("androidx.activity:activity-compose:1.8.2") // Fungsi setContent

    // --- Versi Dependensi Lain ---
    val cameraXVersion = "1.3.3"
    val tfliteVersion = "2.16.1"

    // --- CameraX ---
    implementation("androidx.camera:camera-core:${cameraXVersion}")
    implementation("androidx.camera:camera-camera2:${cameraXVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraXVersion}")
    implementation("androidx.camera:camera-view:${cameraXVersion}")

    // --- Compose Icons ---
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // --- TensorFlow Lite (TFLite) & Vision ---
    // Library untuk memproses gambar
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
//    implementation("org.tensorflow:tensorflow-lite-gpu:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")

    // --- Kotlin Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Testing (tetap) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.compose.ui:ui-text-google-fonts:1.10.0")
}