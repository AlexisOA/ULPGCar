package com.alexisoa.ulpgcar.domain.interactor.routes

import com.alexisoa.ulpgcar.data.model.Features
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng

interface RoutesInteractor {

    suspend fun getListRoutes(coordinatesInit: LatLng, coordinatesFin: LatLng) : Resource<List<Features>>
}