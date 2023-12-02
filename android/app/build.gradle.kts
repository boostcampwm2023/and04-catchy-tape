import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("catchytape.android.hilt")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.ohdodok.catchytape"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ohdodok.catchytape"
        minSdk = 26
        targetSdk = 33
        versionCode = 2
        versionName = "0.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = keystoreProperties["debug.keyAlias"] as String
            keyPassword = keystoreProperties["debug.keyPassword"] as String
            storeFile = file(keystoreProperties["debug.storeFile"] as String)
            storePassword = keystoreProperties["debug.storePassword"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(project(":feature:home"))
    implementation(project(":feature:login"))
    implementation(project(":feature:upload"))
    implementation(project(":feature:player"))
    implementation(project(":feature:playlist"))
    implementation(project(":feature:mypage"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))

    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.timber)

    testImplementation(libs.junit)

    implementation(libs.media3.session)
}