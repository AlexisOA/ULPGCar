package com.alexisoa.ulpgcar.data.repository.routes

import com.alexisoa.ulpgcar.data.model.Features
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng

interface DateRouteRepo {
    suspend fun getRoutesByCoordinates(coordinatesInit: LatLng, coordinatesFin: LatLng): Resource<List<Features>>
}