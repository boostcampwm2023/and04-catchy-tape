@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    testImplementation(libs.junit)
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation ("io.kotest:kotest-property:5.8.0")
    testImplementation ("io.kotest:kotest-extensions-junitxml:5.8.0")
    implementation("javax.inject:javax.inject:1")
}
