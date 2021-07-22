package com.alexisoa.ulpgcar.data.repository.routes

import com.alexisoa.ulpgcar.data.model.Features
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng

interface RoutesRepo {
    suspend fun getRoutesList(coordinatesInit: LatLng, coordinatesFin: LatLng) : Resource<List<Features>>
}