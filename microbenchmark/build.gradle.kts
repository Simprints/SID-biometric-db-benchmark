
plugins {
    id("com.android.library")
    id("kotlin-android")
    id("androidx.benchmark")
}

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 32

        // Set this argument to capture profiling information, instead of measuring performance.
        // Can be one of:
        //   * None
        //   * StackSampling
        //   * MethodTracing
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,LOW-BATTERY,ACTIVITY-MISSING"

    }

    testBuildType = "release"

    buildTypes {
        getByName("release") {
            // The androidx.benchmark plugin configures release buildType with proper settings, such as:
            // - disables code coverage
            // - adds CPU clock locking task
            // - signs release buildType with debug signing config
            // - copies benchmark results into build/outputs/connected_android_test_additional_output folder
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_18.toString()
    }

    namespace = "com.example.benchmark"
}

// [START benchmark_dependency]
dependencies {

    androidTestImplementation(libs.room.core)
    androidTestImplementation(libs.room.ktx)
    androidTestImplementation(libs.sqlCipher)

    androidTestImplementation(project(":benchmarkable"))
    androidTestImplementation(libs.benchmark)

    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlin.stdlib)

    // duplicate the dependencies of ui module here, to ensure we have the same versions
    androidTestImplementation(libs.appcompat)
    androidTestImplementation(libs.cardview)
    androidTestImplementation(libs.constraintlayout)
    androidTestImplementation(libs.recyclerview)
    // [END_EXCLUDE]
}
// [END benchmark_dependency]
