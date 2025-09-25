import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "2.2.0"
    id("com.google.devtools.ksp")
    id("com.anthonycr.plugins.mezzanine") version "2.1.0"
}

android {
    namespace = "com.quxer7.adfilter"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
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

    kotlin {
        jvmToolchain(17)

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

mezzanine {
    files = files(
        "src/main/java/com/quxer7/adfilter/script/raws/element_hiding.js",
        "src/main/java/com/quxer7/adfilter/script/raws/elemhide_blocked.js",
        "src/main/java/com/quxer7/adfilter/script/raws/extended-css.min.js",
        "src/main/java/com/quxer7/adfilter/script/raws/inject.js",
        "src/main/java/com/quxer7/adfilter/script/raws/scriptlets.min.js",
        "src/main/java/com/quxer7/adfilter/script/raws/scriptlets_inject.js"
    )
}


dependencies {
    implementation(project(":adblockclient"))
    implementation(libs.core)
    ksp(libs.processor)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}