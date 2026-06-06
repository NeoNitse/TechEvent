package com.sv.techevent.domain.model

data class Event(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val status: String,
    val bannerUrl: String,
    val description: String,
    val speakers: List<Speaker>,
    val agenda: List<AgendaItem>,
    val venue: Venue
)

data class Speaker(
    val name: String,
    val role: String,
    val avatarUrl: String
)

data class AgendaItem(
    val time: String,
    val activity: String
)

data class Venue(
    val name: String,
    val address: String
)