package com.alexisoa.ulpgcar.data.repository.vehicles

import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.valueobject.Resource
import com.alexisoa.ulpgcar.valueobject.RetrofitClient

class DataVehiclesImp : DataVehiclesRepo {
    override suspend fun getVehicleByName(nameVehicle: String): Resource<List<Vehicle>> {
        return Resource.Success(RetrofitClient.webserviceVehicles.getVehicleByName(nameVehicle.replace(' ', '+')).vehiclesList)
    }
}