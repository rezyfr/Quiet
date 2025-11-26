package id.rezyfr.quiet.screen.main.rules

import androidx.lifecycle.ViewModel
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens

class RulesScreenViewModel(private val navigator: AppComposeNavigator) : ViewModel() {
    fun navigateToAddRules() {
        navigator.navigate(QuietScreens.AddRules.route)
    }
}
