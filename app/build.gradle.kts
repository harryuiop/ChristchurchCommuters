import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.busapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.busapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        localProperties.load(FileInputStream(rootProject.file("local.properties")))
        val metroApiKey = localProperties.getProperty("METRO_API_KEY", "")
        val metroApiUrl = localProperties.getProperty("METRO_API_URL", "")
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY", "")
        val routesApiUrl = localProperties.getProperty("ROUTES_API_URL", "")
        buildConfigField("String", "METRO_API_KEY", metroApiKey)
        buildConfigField("String", "METRO_API_URL", metroApiUrl)
        buildConfigField("String", "MAPS_API_KEY", mapsApiKey)
        buildConfigField("String", "ROUTES_API_URL", routesApiUrl)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation("com.google.android.libraries.places:places:2.6.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("io.insert-koin:koin-android:3.1.4")

    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.10"))
    implementation("com.google.android.libraries.places:places:3.5.0")
}