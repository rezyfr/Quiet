package id.rezyfr.quiet.di

import id.rezyfr.quiet.screen.action.ActionPickerScreenViewModel
import id.rezyfr.quiet.screen.criteria.CriteriaViewModel
import id.rezyfr.quiet.screen.main.rules.RulesScreenViewModel
import id.rezyfr.quiet.screen.pickapp.PickAppViewModel
import id.rezyfr.quiet.screen.picktime.PickTimeViewModel
import id.rezyfr.quiet.screen.rule.AddRuleScreenViewModel
import id.rezyfr.quiet.screen.welcome.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { WelcomeViewModel(get()) }
    viewModel { RulesScreenViewModel(get(), get()) }
    viewModel { AddRuleScreenViewModel(get(), get(), get()) }
    viewModel { PickAppViewModel(get()) }
    viewModel { CriteriaViewModel(get()) }
    viewModel { ActionPickerScreenViewModel(get()) }
    viewModel { PickTimeViewModel(get()) }
}
