import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "catchytape.android.library"
            implementationClass = "gradle.plugin.AndroidLibraryPlugin"
        }
        register("androidFeature") {
            id = "catchytape.android.feature"
            implementationClass = "gradle.plugin.AndroidFeaturePlugin"
        }
        register("androidHilt") {
            id = "catchytape.android.hilt"
            implementationClass = "gradle.plugin.AndroidHiltPlugin"
        }
    }
}