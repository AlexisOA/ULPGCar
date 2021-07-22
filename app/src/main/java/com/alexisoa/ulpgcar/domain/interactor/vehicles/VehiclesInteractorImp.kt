package com.alexisoa.ulpgcar.domain.interactor.vehicles;

import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.data.repository.vehicles.VehiclesRepo
import com.alexisoa.ulpgcar.valueobject.Resource

class VehiclesInteractorImp(private val repository: VehiclesRepo) : VehiclesInteractor{
    override suspend fun getLisVehicles(): Resource<List<Vehicle>> {
        val url = "api/?format=json&select=make&api_key=Your_Database_Api_Key"
        return repository.getVehiclesList(url)
    }
}
