plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf")
}

android {
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        compose = true
    }

    defaultConfig {
        applicationId = "org.bitcoindevkit.devkitwallet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "v0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    namespace = "org.bitcoindevkit.devkitwallet"
}

dependencies {
    // Basic android dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.datastore:datastore:1.1.0")
    implementation("com.google.protobuf:protobuf-javalite:3.18.0")

    // Jetpack Compose
    // Adding the Bill of Materials synchronizes dependencies in the androidx.compose namespace
    // You can remove the library version in your dependency declarations
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.23.1")

    // Toolbar
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Bitcoin Development Kit
    implementation("org.bitcoindevkit:bdk-android:1.0.0-alpha.9")

    // QR codes
    implementation("com.google.zxing:core:3.4.1")

    // Tests
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.0"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
