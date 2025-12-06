plugins {
    id("quiet.android.library")
    id("quiet.android.library.compose")
}

android { namespace = "id.rezyfr.quiet.navigation" }

dependencies {
    implementation(project(":domain"))
    implementation(libs.kotlinx.serialization.json)

    api(libs.androidx.navigation.compose)
}
