plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.photosi.assignment.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "PHOTOFORSE_BASE_URL", "\"https://api.photoforse.online/\"")
        buildConfigField("String", "PHOTOFORSE_API_KEY", "\"AIzaSyCccmdkjGe_9Yt-INL2rCJTNgoS4CXsRDc\"")
        buildConfigField("String", "CATBOX_BASE_URL", "\"https://catbox.moe/\"")
    }

    buildFeatures {
        buildConfig = true
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
    implementation(project(":db"))
    implementation(project(":domain"))
    implementation(project(":time-utils"))

    implementation(libs.koin.core)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.converter.scalars)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.documentfile)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.work.runtime.ktx)
    implementation(libs.koin.androidx.workmanager)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}