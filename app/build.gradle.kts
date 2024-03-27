plugins {
    alias(libs.plugins.androidApplication)
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



// Java language implementation
    //noinspection UseTomlInstead
    implementation ("androidx.navigation:navigation-fragment:2.8.0-alpha05")
    //noinspection UseTomlInstead
    implementation ("androidx.navigation:navigation-ui:2.8.0-alpha05")

    // Feature module Support
    //noinspection UseTomlInstead
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.8.0-alpha05")

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