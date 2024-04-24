plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyectopaisanogo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyectopaisanogo"
        minSdk = 34
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")


// Java language implementation
    //noinspection UseTomlInstead
    implementation ("androidx.navigation:navigation-fragment:2.8.0-alpha05")
    //noinspection UseTomlInstead
    implementation ("androidx.navigation:navigation-ui:2.8.0-alpha05")

    // Feature module Support
    //noinspection UseTomlInstead
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.8.0-alpha05")
    implementation(libs.play.services.maps)

    // Testing Navigation
    //noinspection UseTomlInstead
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.0-alpha05")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}