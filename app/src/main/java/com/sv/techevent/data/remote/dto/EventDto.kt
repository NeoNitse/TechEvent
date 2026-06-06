package com.sv.techevent.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EventDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("date") val date: String,
    @SerializedName("location") val location: String,
    @SerializedName("status") val status: String,
    @SerializedName("bannerUrl") val bannerUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("speakers") val speakers: List<SpeakerDto>,
    @SerializedName("agenda") val agenda: List<AgendaItemDto>,
    @SerializedName("venue") val venue: VenueDto
)

data class SpeakerDto(
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("avatarUrl") val avatarUrl: String
)

data class AgendaItemDto(
    @SerializedName("time") val time: String,
    @SerializedName("activity") val activity: String
)

data class VenueDto(
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String
)