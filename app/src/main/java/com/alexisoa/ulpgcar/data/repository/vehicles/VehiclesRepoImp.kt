package com.alexisoa.ulpgcar.data.repository.vehicles

import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.valueobject.Resource

class VehiclesRepoImp(private val dataVehicles: DataVehiclesRepo) : VehiclesRepo {
    override suspend fun getVehiclesList(nameVehicle: String): Resource<List<Vehicle>> {
        return dataVehicles.getVehicleByName(nameVehicle)
    }
}