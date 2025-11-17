package id.rezyfr.quiet.di

import id.rezyfr.quiet.screen.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { WelcomeViewModel() }
}