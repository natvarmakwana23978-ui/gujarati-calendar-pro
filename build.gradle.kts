// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// જો તમારે repositories અહીં જોડવી હોય તો
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // આ લાઈન ઉમેરો
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // આ લાઈન પણ ઉમેરો
    }
}
