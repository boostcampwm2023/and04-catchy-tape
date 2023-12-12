@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("catchytape.android.feature")
}

android {
    namespace = "com.ohdodok.catchytape.feature.home"

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

}