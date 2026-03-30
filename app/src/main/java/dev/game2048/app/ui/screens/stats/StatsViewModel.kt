package dev.game2048.app.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.game2048.app.data.repository.StatsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class StatsViewModel @Inject constructor(private val repository: StatsRepository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = repository.stats
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())

    init {
        viewModelScope.launch { repository.load() }
    }
}
