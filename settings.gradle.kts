// settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()   // અહીંયા Google Maven Repository ઉમેરવામાં આવ્યું છે
        mavenCentral()
    }
}

// આ લાઇન તમારા પ્રોજેક્ટ મોડ્યુલને ઉમેરે છે
include(":app")
