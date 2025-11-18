package id.rezyfr.quiet.navigation

import org.koin.dsl.module

val navigationModule = module {
    single<AppComposeNavigator> { QuietComposeNavigator() }
}