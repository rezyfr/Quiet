package id.rezyfr.quiet.screen.rule

import android.R.attr.action
import android.R.attr.text
import android.content.pm.PackageManager
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.rezyfr.quiet.data.repository.NotificationRepository
import id.rezyfr.quiet.data.repository.RuleRepository
import id.rezyfr.quiet.domain.ExtraCriteria
import id.rezyfr.quiet.domain.NotificationUiModel
import id.rezyfr.quiet.domain.Rule
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.screen.pickapp.AppItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddRuleScreenViewModel(
    private val navigator: AppComposeNavigator,
    private val repository: NotificationRepository,
    private val ruleRepository: RuleRepository
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

    fun navigateToPickTime() {
        navigator.navigate(QuietScreens.PickTime.route)
    }

    fun addExtraCriteria(extraCriteria: ExtraCriteria) {
        _state.update {
            it.copy(
                selectedExtraCriteria =
                it.selectedExtraCriteria.toMutableList().apply {
                    if (contains(extraCriteria)) {
                        remove(extraCriteria)
                    } else {
                        add(extraCriteria)
                    }
                }
            )
        }
    }

    fun getRecentNotification(pm: PackageManager, packageName: String?) {
        viewModelScope.launch {
            repository.getRecentNotifications(packageName).collect { notifEntity ->
                _state.update {
                    it.copy(
                        notificationList =
                        notifEntity.map { notif ->
                            val info =
                                try {
                                    pm.getApplicationInfo(notif.packageName, 0)
                                } catch (e: PackageManager.NameNotFoundException) {
                                    print(e)
                                    null
                                }

                            Pair(
                                NotificationUiModel(
                                    notif.sbnKey,
                                    notif.packageName,
                                    notif.title,
                                    notif.text,
                                    parsedToTime(notif.postTime),
                                ),
                                AppItem(
                                    label = info?.loadLabel(pm).toString(),
                                    icon = info?.loadIcon(pm),
                                    packageName = notif.packageName,
                                ),
                            )
                        }
                    )
                }
            }
        }
    }

    private fun parsedToTime(time: Long): String {
        return try {
            val date = Date(time)

            val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            val formattedTime = timeFormatter.format(date)

            formattedTime
        } catch (e: Exception) {
            ""
        }
    }

    fun getExtraCriteria() {
        _state.update { it.copy(extraCriteriaList = ExtraCriteria.DEFAULT) }
    }

    fun saveRule() {
        val state = _state.value
        val appText = (state.appItem?.label ?: "any app") + " that "
        val criteria = if (state.criteriaText.isEmpty()) {
            "contains anything "
        } else {
            "contains ${
                state.criteriaText.fastJoinToString(" or ") {
                    "\"${it.capitalize()}\""
                }
            } "
        }
        val actions = "then ${state.action?.title ?: "do nothing"} "
        val text = "When I get a notification from " + appText + criteria + actions
        viewModelScope.launch {
            ruleRepository.saveRule(
                Rule(
                    packageName = listOf(_state.value.appItem?.packageName ?: ""),
                    keywords = _state.value.criteriaText,
                    dayRange = null,
                    text = text,
                    action = _state.value.action!!,
                    enabled = true
                )
            )
            navigator.navigateUp()
        }
    }

    data class AddRuleScreenState(
        val appItem: AppItem? = null,
        val criteriaText: List<String> = emptyList(),
        val action: ActionItem? = null,
        val notificationList: List<Pair<NotificationUiModel, AppItem>> = emptyList(),
        val extraCriteriaList: List<ExtraCriteria> = emptyList(),
        val selectedExtraCriteria: List<ExtraCriteria> = emptyList(),
    )
}
