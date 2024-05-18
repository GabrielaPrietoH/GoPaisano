import org.gradle.api.internal.artifacts.dsl.dependencies.DependenciesExtensionModule.module
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyectopaisanogo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyectopaisanogo"
        minSdk = 29
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

    implementation(platform(libs.firebase.bom))
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.firebaseui:firebase-ui-firestore:7.1.1")

    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    implementation (libs.glide)

// Java language implementation
    //noinspection UseTomlInstead
    implementation ("androidx.navigation:navigation-fragment:2.8.0-alpha07")
    //noinspection UseTomlInstead
    implementation ("androidx.navigation:navigation-ui:2.8.0-alpha07")

    // Feature module Support
    //noinspection UseTomlInstead
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.8.0-alpha07")
    implementation(libs.play.services.maps)

    // Testing Navigation
    //noinspection UseTomlInstead
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.0-alpha07")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


        implementation("androidx.recyclerview:recyclerview:1.2.1")



}