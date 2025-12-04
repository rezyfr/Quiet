package id.rezyfr.quiet.screen.rule

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.rezyfr.quiet.data.repository.NotificationRepository
import id.rezyfr.quiet.data.repository.RuleRepository
import id.rezyfr.quiet.domain.model.BatchAction
import id.rezyfr.quiet.domain.model.BluetoothCriteria
import id.rezyfr.quiet.domain.model.CallCriteria
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.CriteriaType
import id.rezyfr.quiet.domain.model.DismissAction
import id.rezyfr.quiet.domain.model.NotificationUiModel
import id.rezyfr.quiet.domain.model.PostureCriteria
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.domain.model.RuleCriteria
import id.rezyfr.quiet.domain.model.TimeCriteria
import id.rezyfr.quiet.domain.model.getCriteriaTypes
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import id.rezyfr.quiet.screen.action.ActionItem
import id.rezyfr.quiet.screen.pickapp.AppItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRuleScreenViewModel(
    private val navigator: AppComposeNavigator,
    private val repository: NotificationRepository,
    private val ruleRepository: RuleRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AddRuleScreenState())
    val state = _state.asStateFlow()

    fun setAppItem(appItem: List<AppItem>) {
        _state.update { it.copy(selectedApps = it.selectedApps.toMutableList().apply {
            addAll(appItem)
        }) }
    }

    fun setCriteria(criteria: List<String>) {
        _state.update { it.copy(criteriaText = criteria) }
    }

    fun setAction(action: ActionItem) {
        _state.update { it.copy(action = action) }
    }

    fun navigateToPickApp() {
        val pickedApps = _state.value.selectedApps.map { it.packageName }
        navigator.navigate(
            QuietScreens.PickApp.createRoute(pickedApps)
        )
    }

    fun navigateToPickCriteria() {
        val selectedCriteria = _state.value.criteriaText
        navigator.navigate(QuietScreens.Criteria.createRoute(selectedCriteria))
    }

    fun navigateToPickAction() {
        navigator.navigate(QuietScreens.Action.route)
    }

    fun navigateToPickTime() {
        navigator.navigate(QuietScreens.PickTime.route)
    }

    fun getRecentNotification(
        pm: PackageManager
    ) {
        val packageName = _state.value.selectedApps.map { it.packageName }
        val phrases = _state.value.criteriaText

        viewModelScope.launch {
            repository.getRecentNotifications(packageName, phrases).collect { notifEntity ->
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

    fun addCriteria(type: CriteriaType) {
        val newCriteria: RuleCriteria = when (type) {
            CriteriaType.TIME -> TimeCriteria(mode = "during_schedule", ranges = null)
            CriteriaType.CALL -> CallCriteria(status = "on_call")
            CriteriaType.BLUETOOTH -> BluetoothCriteria(mode = "any")
            CriteriaType.POSTURE -> PostureCriteria(posture = "face_down")
        }

        _state.update {
            it.copy(
                selectedCriteria = it.selectedCriteria + newCriteria
            )
        }
    }

    fun saveRule() {
        val s = _state.value

        val rule = Rule(
            id = 0,
            name = "",
            enabled = true,
            apps = buildApps(),
            keywords = buildKeywords(),
            criteria = buildCriteria(),
            action = s.action!!.toDomainAction() // conversion below
        )

        viewModelScope.launch {
            ruleRepository.saveRule(rule)
            navigator.navigateUp()
        }
    }

    private fun ActionItem.toDomainAction(): RuleAction =
        when (this.id) {
            "dismiss_immediate" -> DismissAction(immediately = true, delayMs = null)
            "dismiss_delay" -> DismissAction(immediately = false, delayMs = null)
            "cooldown" -> CooldownAction(target = "app", durationMs = 0L)
            "batch" -> BatchAction(mode = "during_schedule", schedule = null)
            else -> error("Unknown action type")
        }

    private fun buildApps(): List<String> =
        _state.value.selectedApps.map { it.packageName }

    private fun buildKeywords(): List<String> =
        _state.value.criteriaText.ifEmpty { listOf("") } // empty means "match anything"

    private fun buildCriteria(): List<RuleCriteria> =
        _state.value.selectedCriteria.map { extra ->
            when (extra) {
                is TimeCriteria -> {
                    TimeCriteria(
                        mode = "during_schedule",
                        ranges = extra.ranges
                    )
                }
                is CallCriteria -> {
                    CallCriteria(status = "on_call")
                }
                is BluetoothCriteria -> {
                    BluetoothCriteria(mode = "any")
                }
                else -> error("Unsupported criteria")
            }
        }

    fun getAvailableCriteria(): List<CriteriaType> {
        return getCriteriaTypes(selected = _state.value.selectedCriteria)
    }

    data class AddRuleScreenState(
        val selectedApps: List<AppItem> = listOf(),
        val criteriaText: List<String> = emptyList(),
        val action: ActionItem? = null,
        val notificationList: List<Pair<NotificationUiModel, AppItem>> = emptyList(),
        val selectedCriteria: List<RuleCriteria> = emptyList(),
    )
}
