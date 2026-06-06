package com.sv.techevent.data.remote

import com.sv.techevent.data.remote.dto.EventResponse
import retrofit2.http.GET

interface ApiService {
    @GET("events.json")
    suspend fun getEvents(): EventResponse
}