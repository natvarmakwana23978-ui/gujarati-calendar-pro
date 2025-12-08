plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.22"
}

android {
    namespace = "com.gujaraticalendar"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.gujaraticalendar"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    
    // ✅ આ નવું ઉમેરો:
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    // ✅ આ પણ ઉમેરો:
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}
