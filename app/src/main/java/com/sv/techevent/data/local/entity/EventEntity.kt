package com.sv.techevent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val date: String,
    val location: String,
    val status: String,
    val bannerUrl: String,
    val description: String
)