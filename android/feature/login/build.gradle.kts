import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("catchytape.android.feature")
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
localProperties.load(FileInputStream(localPropertiesFile))

android {
    namespace = "com.ohdodok.catchytape.feature.login"

    defaultConfig {
        buildConfigField("String", "GOOGLE_CLIENT_ID", localProperties["google.client.id"] as String)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.google.play.services)
}