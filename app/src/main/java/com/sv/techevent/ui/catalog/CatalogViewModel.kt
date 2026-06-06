package com.sv.techevent.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sv.techevent.data.repository.EventRepository
import com.sv.techevent.domain.model.Event
import com.sv.techevent.domain.model.UIState
import com.sv.techevent.ui.theme.ThemePreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: EventRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<List<Event>>>(UIState.Loading)
    val uiState: StateFlow<UIState<List<Event>>> = _uiState

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            themePreferences.isOfflineMode.collect { forceOffline ->
                loadEvents(forceOffline)
            }
        }
    }

    private fun loadEvents(forceOffline: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UIState.Loading
            repository.getEvents(forceOffline).collect { (events, offline) ->
                _isOffline.value = offline
                _uiState.value = UIState.Success(events)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val forceOffline = themePreferences.isOfflineMode.first()
            _isRefreshing.value = true
            repository.getEvents(forceOffline).collect { (events, offline) ->
                _isOffline.value = offline
                _uiState.value = UIState.Success(events)
                _isRefreshing.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(event: Event, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(event, isFavorite)
        }
    }

    fun isFavorite(eventId: String) = repository.isFavorite(eventId)

    companion object {
        fun factory(
            repository: EventRepository,
            themePreferences: ThemePreferences
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CatalogViewModel(repository, themePreferences) as T
            }
        }
    }
}