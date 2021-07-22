package com.alexisoa.ulpgcar.data.repository.vehicles

import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.valueobject.Resource

interface VehiclesRepo {
    suspend fun getVehiclesList(nameVehicle: String): Resource<List<Vehicle>>
}