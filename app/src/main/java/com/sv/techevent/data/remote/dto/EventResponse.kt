package com.sv.techevent.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("events") val events: List<EventDto>
)