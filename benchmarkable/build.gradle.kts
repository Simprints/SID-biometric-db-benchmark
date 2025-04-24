plugins {
    id("com.android.library")
    id("kotlin-android")
    alias (libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    compileSdk = 35
    namespace = "com.example.benchmark.ui"

    defaultConfig {
        minSdk = 32
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_18.toString()
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.kotlin.stdlib)
    implementation(libs.recyclerview)
    implementation(libs.room.core)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.sqlCipher)


    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.test.runner)

    testImplementation(libs.junit)
}
