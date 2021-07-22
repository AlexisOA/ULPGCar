package com.alexisoa.ulpgcar.data.repository.location

import com.alexisoa.ulpgcar.data.model.Places
import retrofit2.http.GET
import retrofit2.http.Url

interface StreetMapWS {
    @GET
    suspend fun getLocationByName(@Url url: String): ArrayList<Places>
}