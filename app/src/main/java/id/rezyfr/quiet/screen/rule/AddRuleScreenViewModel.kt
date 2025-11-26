package id.rezyfr.quiet.screen.rule

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.rezyfr.quiet.data.repository.NotificationRepository
import id.rezyfr.quiet.domain.NotificationModel
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.screen.pickapp.AppItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddRuleScreenViewModel(
    private val navigator: AppComposeNavigator,
    private val repository: NotificationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AddRuleScreenState())
    val state = _state.asStateFlow()

    fun setAppItem(appItem: AppItem?) {
        _state.update { it.copy(appItem = appItem) }
    }

    fun setCriteria(criteria: List<String>) {
        _state.update { it.copy(criteriaText = criteria) }
    }

    fun setAction(action: ActionItem) {
        _state.update { it.copy(action = action) }
    }

    fun navigateToPickApp() {
        navigator.navigate(QuietScreens.PickApp.route)
    }

    fun navigateToPickCriteria() {
        navigator.navigate(QuietScreens.Criteria.route)
    }

    fun navigateToPickAction() {
        navigator.navigate(QuietScreens.Action.route)
    }

    fun getRecentNotification(pm: PackageManager, packageName: String?) {
        viewModelScope.launch {
            repository.getNotification(packageName)
                .collect { notifEntity ->
                    _state.update {
                        it.copy(
                            notificationList = notifEntity.map { notif ->
                                val info = pm.getApplicationInfo(notif.packageName, 0)

                                Pair(
                                    NotificationModel(
                                        notif.sbnKey,
                                        notif.packageName,
                                        notif.title,
                                        notif.text,
                                        notif.postTime,
                                        notif.saved,
                                    ),
                                    AppItem(
                                        label = info.loadLabel(pm).toString(),
                                        icon = info.loadIcon(pm),
                                        packageName = notif.packageName
                                    )
                                )
                            }
                        )
                    }
                }
        }
    }

    data class AddRuleScreenState(
        val appItem: AppItem? = null,
        val criteriaText: List<String> = emptyList(),         // null -> "anything"
        val action: ActionItem? = null,
        val notificationList: List<Pair<NotificationModel, AppItem>> = emptyList()
    )
}