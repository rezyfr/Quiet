package id.rezyfr.quiet.screen.rule

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.rezyfr.quiet.domain.model.BatchAction
import id.rezyfr.quiet.domain.model.BluetoothCriteria
import id.rezyfr.quiet.domain.model.CallCriteria
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.CriteriaType
import id.rezyfr.quiet.domain.model.NotificationUiModel
import id.rezyfr.quiet.domain.model.PostureCriteria
import id.rezyfr.quiet.domain.model.Rule
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.domain.model.RuleCriteria
import id.rezyfr.quiet.domain.model.TimeCriteria
import id.rezyfr.quiet.domain.model.TimeRange
import id.rezyfr.quiet.domain.model.getCriteriaTypes
import id.rezyfr.quiet.domain.repository.NotificationRepository
import id.rezyfr.quiet.domain.repository.RuleRepository
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
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

    fun setAppItem(appItems: List<AppItem>) {
        _state.update { it.copy(selectedApps = appItems) }
    }

    fun setCriteria(criteria: List<String>) {
        _state.update { it.copy(criteriaText = criteria) }
    }

    fun setAction(action: RuleAction) {
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

    fun navigateToPickTime(type: String) {
        val selectedTime = _state.value.selectedCriteria.find { it is TimeCriteria } as? TimeCriteria?
        navigator.navigate(QuietScreens.PickTime.createRoute(
            pickTime = selectedTime?.ranges ?: emptyList(),
            type = type
        ))
    }

    fun getRecentNotification(
        pm: PackageManager
    ) {
        val packageName = _state.value.selectedApps.map { it.packageName }
        val phrases = _state.value.criteriaText
        val timeSpan = _state.value.selectedCriteria.find { it is TimeCriteria } as? TimeCriteria?

        viewModelScope.launch {
            repository.getRecentNotifications(
                packageName,
                phrases,
                timeSpan?.ranges.orEmpty()
            ).collect { notifEntity ->
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

    fun addTimeCriteria(timeRanges: List<TimeRange>) {
        _state.update {
            it.copy(
                selectedCriteria = it.selectedCriteria.map { criteria ->
                    if (criteria is TimeCriteria) {
                        criteria.copy(
                            ranges = timeRanges
                        )
                    } else {
                        criteria
                    }
                }
            )
        }
    }

    fun addCriteria(type: CriteriaType) {
        val newCriteria: RuleCriteria = when (type) {
            CriteriaType.TIME -> TimeCriteria(ranges = listOf())
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

    fun setCooldownTime(times: Long) {
        _state.update {
            it.copy(
                action = (it.action as? CooldownAction)?.copy(
                    durationMs = times
                )
            )
        }
    }

    fun setBatchScheduleWindow(timeRanges: List<TimeRange>) {
        _state.update {
            it.copy(
                action = (it.action as? BatchAction)?.copy(
                    schedule = timeRanges
                )
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
            action = s.action!!
        )

        viewModelScope.launch {
            ruleRepository.saveRule(rule)
            navigator.navigateUp()
        }
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

    fun getAvailableCooldownTimes() = listOf<Long>(
        60000,
        180000,
        300000,
        600000,
        3600000,
        10800000,
        30000000,
    )

    data class AddRuleScreenState(
        val selectedApps: List<AppItem> = listOf(),
        val criteriaText: List<String> = emptyList(),
        val action: RuleAction? = null,
        val notificationList: List<Pair<NotificationUiModel, AppItem>> = emptyList(),
        val selectedCriteria: List<RuleCriteria> = emptyList(),
    )
}
