package id.rezyfr.quiet.screen.pickapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.navigation.QuietScreens
import id.rezyfr.quiet.util.getAppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PickAppViewModel(private val navigator: AppComposeNavigator) : ViewModel() {
    private val _state = MutableStateFlow(PickAppUiState())
    val state: StateFlow<PickAppUiState> = _state
    private var _pendingQuery = ""

    fun getInstalledApps(packageManager: PackageManager) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val apps = loadApps(packageManager)

            _state.update {
                it.copy(
                    isLoading = false,
                    allApps = apps,
                    filteredApps =
                    if (_pendingQuery.isNotEmpty()) {
                        apps.filter { apps ->
                            apps.label.contains(_pendingQuery, ignoreCase = true)
                        }
                    } else {
                        apps
                    },
                )
            }
            _pendingQuery = ""
        }
    }

    fun updateSearch(query: String) {
        if (_state.value.isLoading) {
            _pendingQuery = query
            return
        }
        _state.update {
            it.copy(
                searchQuery = query,
                filteredApps =
                if (query.isNotEmpty()) {
                    it.allApps.filter { apps -> apps.label.contains(query, ignoreCase = true) }
                } else {
                    it.allApps
                },
            )
        }
    }

    fun selectApp(app: AppItem) {
        _state.update {
            it.copy(
                selectedApp = it.selectedApp.toMutableList().apply {
                    add(app)
                }
            )
        }
    }

    fun setPickedApps(pickedApps: List<String>, pm: PackageManager) {
        _state.update {
            it.copy(
                selectedApp = getAppItem(pm, pickedApps)
            )
        }
    }

    fun pickApp() {
        navigator.navigateBackWithResult(
            "key_pick_apps",
            _state.value.selectedApp.map {
                it.packageName
            },
            QuietScreens.AddRules.route,
        )
    }

    data class PickAppUiState(
        val isLoading: Boolean = true,
        val allApps: List<AppItem> = emptyList(),
        val filteredApps: List<AppItem> = emptyList(),
        val selectedApp: List<AppItem> = emptyList(),
        val searchQuery: String = "",
    )

    private suspend fun loadApps(pm: PackageManager): List<AppItem> =
        withContext(Dispatchers.IO) {
            val intent =
                Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
            val resolveInfos = pm.queryIntentActivities(intent, 0)

            resolveInfos
                .sortedBy { it.loadLabel(pm).toString().lowercase() }
                .map { info ->
                    val label = info.loadLabel(pm).toString()
                    val packageName = info.activityInfo.packageName
                    val icon = info.loadIcon(pm)

                    AppItem(
                        label = label,
                        icon = icon, // keep raw drawable here
                        packageName = packageName,
                    )
                }
        }
}
