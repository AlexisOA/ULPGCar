package com.alexisoa.ulpgcar.domain.interactor.routes

import com.alexisoa.ulpgcar.data.model.Features
import com.alexisoa.ulpgcar.data.repository.routes.RoutesRepo
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng

class RoutesInteractorImp(private val repository: RoutesRepo):RoutesInteractor {
    override suspend fun getListRoutes(coordinatesInit: LatLng, coordinatesFin: LatLng): Resource<List<Features>> {
        return repository.getRoutesList(coordinatesInit, coordinatesFin)
    }
}