package com.alexisoa.ulpgcar.domain.interactor.vehicles

import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.valueobject.Resource

interface VehiclesInteractor {
    suspend fun getLisVehicles(): Resource<List<Vehicle>>
}