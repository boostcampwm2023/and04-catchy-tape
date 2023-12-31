// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.navigation.safe.args) apply false
}


tasks.register<Exec>("domainUnitTest") {
    commandLine = listOf("gradle", "core:domain:test")
}


tasks.register<Exec>("debugUnitTest") {
    dependsOn("domainUnitTest")
    commandLine = listOf("gradle", "testDebugUnitTest")
}


true // Needed to make the Suppress annotation work for the plugins block