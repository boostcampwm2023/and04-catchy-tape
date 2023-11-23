@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    reports {
        junitXml.required.set(false)
    }
    systemProperty("gradle.build.dir", project.buildDir)
}

dependencies {
    api(libs.coroutines)

    implementation(libs.inject)

    // fixme : kotest 사용이 확정되면 junit 지우기
    testImplementation(libs.junit)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.extentions.junitxml)
    testImplementation(libs.mockk)
}
