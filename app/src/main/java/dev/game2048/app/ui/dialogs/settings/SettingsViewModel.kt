package dev.game2048.app.ui.dialogs.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.game2048.app.data.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: SettingsRepository) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = repository.gridSizeFlow
        .map { SettingsUiState(gridSize = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun applyGridSize(size: Int) {
        viewModelScope.launch { repository.saveGridSize(size) }
    }
}
