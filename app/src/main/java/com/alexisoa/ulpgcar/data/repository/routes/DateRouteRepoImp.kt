package com.alexisoa.ulpgcar.data.repository.routes

import com.alexisoa.ulpgcar.data.model.Features
import com.alexisoa.ulpgcar.valueobject.Resource
import com.alexisoa.ulpgcar.valueobject.RetrofitClient
import com.google.android.gms.maps.model.LatLng

class DateRouteRepoImp: DateRouteRepo {
    override suspend fun getRoutesByCoordinates(coordinatesInit: LatLng, coordinatesFin: LatLng): Resource<List<Features>> {
        val url = "driving-car?api_key=5b3ce3597851110001cf6248a48612e2710145c4ad1041fece996fe6&start=" + coordinatesInit.longitude + "," + coordinatesInit.latitude + "&end=" + coordinatesFin.longitude + "," + coordinatesFin.latitude
        //val url = "driving-car?api_key=5b3ce3597851110001cf6248a48612e2710145c4ad1041fece996fe6&start=-6.07269287109375,36.35495110643483&end=-6.119384765624999,36.69044623523481"
        return Resource.Success(RetrofitClient.webserviceOpenRoute.getRouteByCoordinates(url).features)
    }
}