package com.jose.arappkotlin

import retrofit2.http.GET

interface Api {
    @GET("modelos")
    suspend fun getModelos(): List<Modelo>
}