@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotest.runner)
    testImplementation (libs.kotest.property)
    testImplementation (libs.kotest.extentions.junitxml)
    implementation("javax.inject:javax.inject:1")
}
