package com.sv.techevent.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sv.techevent.data.local.entity.EventEntity
import com.sv.techevent.data.repository.EventRepository
import com.sv.techevent.domain.model.Event
import com.sv.techevent.domain.model.UIState
import com.sv.techevent.domain.model.Venue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<List<Event>>>(UIState.Loading)
    val uiState: StateFlow<UIState<List<Event>>> = _uiState

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { entities ->
                val events = entities.map { it.toEvent() }
                _uiState.value = UIState.Success(events)
            }
        }
    }

    fun removeFavorite(event: Event) {
        viewModelScope.launch {
            repository.toggleFavorite(event, isFavorite = true)
        }
    }

    private fun EventEntity.toEvent() = Event(
        id = id,
        title = title,
        date = date,
        location = location,
        status = status,
        bannerUrl = bannerUrl,
        description = description,
        speakers = emptyList(),
        agenda = emptyList(),
        venue = Venue("", "")
    )

    companion object {
        fun factory(repository: EventRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FavoritesViewModel(repository) as T
                }
            }
    }
}