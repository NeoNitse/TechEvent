package com.sv.techevent.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sv.techevent.data.repository.EventRepository
import com.sv.techevent.domain.model.Event
import com.sv.techevent.domain.model.UIState
import com.sv.techevent.ui.catalog.CatalogScreen
import com.sv.techevent.ui.catalog.CatalogViewModel
import com.sv.techevent.ui.detail.DetailScreen
import com.sv.techevent.ui.detail.DetailViewModel
import com.sv.techevent.ui.theme.ThemePreferences

@Composable
fun ListDetailLayout(
    catalogViewModel: CatalogViewModel,
    detailViewModel: DetailViewModel,
    catalogUiState: UIState<List<Event>>,
    isOffline: Boolean,
    repository: EventRepository,
    themePreferences: ThemePreferences
) {
    var selectedEventId by remember { mutableStateOf<String?>(null) }
    val events = (catalogUiState as? UIState.Success)?.data ?: emptyList()

    Row(modifier = Modifier.fillMaxSize()) {
        CatalogScreen(
            modifier = Modifier.width(380.dp),
            viewModel = catalogViewModel,
            uiState = catalogUiState,
            isOffline = isOffline,
            onEventClick = { eventId -> selectedEventId = eventId }
        )
        selectedEventId?.let { eventId ->
            DetailScreen(
                modifier = Modifier.weight(1f),
                eventId = eventId,
                allEvents = events,
                viewModel = detailViewModel,
                onBack = { selectedEventId = null }
            )
        }
    }
}