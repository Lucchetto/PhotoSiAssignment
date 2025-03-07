plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sqlidelight)
}

android {
    namespace = "com.photosi.assignment.db"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.sqldelight.android.driver)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set(android.namespace + ".db")
            schemaOutputDirectory.set(file("src/main/sqldelight/schemas/AppDatabase"))
            migrationOutputDirectory.set(file("src/main/sqldelight/migrations/AppDatabase"))
        }
    }
}