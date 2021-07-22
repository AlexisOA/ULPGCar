package com.alexisoa.ulpgcar.data.repository.vehicles

import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.data.model.VehiclesList
import retrofit2.http.GET
import retrofit2.http.Url

interface VehiclesWS {

    //Suspend fun es una funcion que va hacer una peticion a un servidor
    //Hasta que no termine no va a retornar nada, ya sea success o error
    @GET
    suspend fun getVehicleByName(@Url url: String): VehiclesList
}