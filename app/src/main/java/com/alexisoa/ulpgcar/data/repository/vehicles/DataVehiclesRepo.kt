package com.alexisoa.ulpgcar.data.repository.vehicles

import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.valueobject.Resource

interface DataVehiclesRepo {
    suspend fun getVehicleByName(nameVehicle: String): Resource<List<Vehicle>>
}