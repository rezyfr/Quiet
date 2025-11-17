package id.rezyfr.quiet.navigation

import android.icu.util.MeasureUnit
import org.koin.dsl.module

val navigationModule = module {
    single<AppComposeNavigator> { QuietComposeNavigator() }
}