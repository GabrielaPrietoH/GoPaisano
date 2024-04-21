import com.android.tools.build.bundletool.flags.Flag.path

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.jetbrains.kotlin.multiplatform") version "1.9.23" apply false
}

