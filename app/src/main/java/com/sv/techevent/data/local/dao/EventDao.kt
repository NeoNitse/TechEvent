package com.sv.techevent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sv.techevent.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("DELETE FROM favorite_events WHERE id = :eventId")
    suspend fun deleteEvent(eventId: String)

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): Flow<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_events WHERE id = :eventId)")
    fun isFavorite(eventId: String): Flow<Boolean>
}