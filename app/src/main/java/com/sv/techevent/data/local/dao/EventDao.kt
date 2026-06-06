package com.sv.techevent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sv.techevent.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    // IGNORE para no sobreescribir el flag isFavorite de filas existentes
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: EventEntity)

    @Query("UPDATE favorite_events SET title = :title, date = :date, location = :location, status = :status, bannerUrl = :bannerUrl, description = :description WHERE id = :id")
    suspend fun updateEventData(
        id: String, title: String, date: String, location: String,
        status: String, bannerUrl: String, description: String
    )

    @Query("UPDATE favorite_events SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: String, fav: Boolean)

    @Query("DELETE FROM favorite_events WHERE id = :eventId")
    suspend fun deleteEvent(eventId: String)

    @Query("SELECT * FROM favorite_events WHERE isFavorite = 1")
    fun getAllFavorites(): Flow<List<EventEntity>>

    @Query("SELECT * FROM favorite_events")
    fun getAllCachedEvents(): Flow<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_events WHERE id = :eventId AND isFavorite = 1)")
    fun isFavorite(eventId: String): Flow<Boolean>
}