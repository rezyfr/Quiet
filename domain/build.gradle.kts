plugins {
    id("quiet.android.library")
    kotlin("plugin.serialization")
}

android { namespace = "id.rezyfr.quiet.domain" }

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
}
