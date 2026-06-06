package com.sv.techevent.data.repository

import com.sv.techevent.data.local.dao.EventDao
import com.sv.techevent.data.local.entity.EventEntity
import com.sv.techevent.data.remote.ApiService
import com.sv.techevent.domain.model.AgendaItem
import com.sv.techevent.domain.model.Event
import com.sv.techevent.domain.model.Speaker
import com.sv.techevent.domain.model.Venue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {

    fun getEvents(forceOffline: Boolean = false): Flow<Pair<List<Event>, Boolean>> = flow {
        if (forceOffline) {
            eventDao.getAllCachedEvents().collect { entities ->
                emit(Pair(entities.map { it.toEvent() }, true))
            }
            return@flow
        }

        try {
            val response = apiService.getEvents()
            val events = response.events.map { dto ->
                Event(
                    id = dto.id,
                    title = dto.title,
                    date = dto.date,
                    location = dto.location,
                    status = dto.status,
                    bannerUrl = dto.bannerUrl,
                    description = dto.description,
                    speakers = dto.speakers.map { Speaker(it.name, it.role, it.avatarUrl) },
                    agenda = dto.agenda.map { AgendaItem(it.time, it.activity) },
                    venue = Venue(dto.venue.name, dto.venue.address)
                )
            }
            // Cachea sin tocar el flag isFavorite
            events.forEach { event ->
                eventDao.insertEvent(
                    EventEntity(
                        id = event.id,
                        title = event.title,
                        date = event.date,
                        location = event.location,
                        status = event.status,
                        bannerUrl = event.bannerUrl,
                        description = event.description,
                        isFavorite = false
                    )
                )
                // Actualiza solo los campos de datos (NO isFavorite)
                eventDao.updateEventData(
                    event.id, event.title, event.date, event.location,
                    event.status, event.bannerUrl, event.description
                )
            }
            emit(Pair(events, false))
        } catch (e: Exception) {
            eventDao.getAllCachedEvents().collect { entities ->
                emit(Pair(entities.map { it.toEvent() }, true))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getFavorites(): Flow<List<EventEntity>> = eventDao.getAllFavorites()

    fun isFavorite(eventId: String): Flow<Boolean> = eventDao.isFavorite(eventId)

    suspend fun toggleFavorite(event: Event, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            // Asegura que el row exista antes de tocar el flag
            eventDao.insertEvent(
                EventEntity(
                    id = event.id,
                    title = event.title,
                    date = event.date,
                    location = event.location,
                    status = event.status,
                    bannerUrl = event.bannerUrl,
                    description = event.description,
                    isFavorite = false
                )
            )
            eventDao.setFavorite(event.id, !isFavorite)
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
}