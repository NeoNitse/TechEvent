package com.sv.techevent.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sv.techevent.data.repository.EventRepository
import com.sv.techevent.domain.model.Event
import com.sv.techevent.domain.model.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<Event>>(UIState.Loading)
    val uiState: StateFlow<UIState<Event>> = _uiState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadEvent(eventId: String, allEvents: List<Event>) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            // Primero busca en los eventos en memoria (datos completos de API)
            val memoryEvent = allEvents.find { it.id == eventId }

            if (memoryEvent != null && memoryEvent.speakers.isNotEmpty()) {
                // Tiene datos completos
                _uiState.value = UIState.Success(memoryEvent)
            } else {
                // Intenta obtener datos frescos de la red
                repository.getEvents().collect { (events, _) ->
                    val freshEvent = events.find { it.id == eventId }
                    if (freshEvent != null) {
                        _uiState.value = UIState.Success(freshEvent)
                    } else if (memoryEvent != null) {
                        // Fallback a datos de Room aunque estén incompletos
                        _uiState.value = UIState.Success(memoryEvent)
                    } else {
                        _uiState.value = UIState.Error("Evento no encontrado")
                    }
                }
            }

            // Observa estado de favorito
            repository.isFavorite(eventId).collect { fav ->
                _isFavorite.value = fav
            }
        }
    }

    fun toggleFavorite(event: Event) {
        viewModelScope.launch {
            repository.toggleFavorite(event, _isFavorite.value)
        }
    }

    companion object {
        fun factory(repository: EventRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetailViewModel(repository) as T
                }
            }
    }
}