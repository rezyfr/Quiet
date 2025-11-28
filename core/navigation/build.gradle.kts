plugins {
    id("quiet.android.library")
    id("quiet.android.library.compose")
}

android { namespace = "id.rezyfr.quiet.navigation" }

dependencies { api(libs.androidx.navigation.compose) }
