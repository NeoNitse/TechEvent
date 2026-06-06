package com.sv.techevent.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL =
        "https://gist.githubusercontent.com/NeoNitse/d594eb5b1461b11d8b3c8c279f019865/raw/1fd52fc6a74cb9fae588e2a1a9a3b28055c4a36e/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}