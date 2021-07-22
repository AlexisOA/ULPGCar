package com.alexisoa.ulpgcar.data.repository.routes

import com.alexisoa.ulpgcar.data.model.FeaturesList
import com.alexisoa.ulpgcar.data.model.Places
import retrofit2.http.GET
import retrofit2.http.Url

interface OpenRoutesWS {
    //Suspend fun es una funcion que va hacer una peticion a un servidor
    //Hasta que no termine no va a retornar nada, ya sea success o error
    @GET
    suspend fun getRouteByCoordinates(@Url url: String): FeaturesList

}