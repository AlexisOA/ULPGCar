package com.alexisoa.ulpgcar.domain.interactor.search

import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng

interface SearchInteractor {

    suspend fun getListLocations(text: String): Resource<ArrayList<Places>>
    suspend fun getTravelsMatch(latitudeDest : Double, longitudeDest: Double, latitudeInit : Double,
                                longitudeInit: Double, searchPolilyne: ArrayList<com.alexisoa.ulpgcar.data.model.LatLng>,
                                date:Long, passengers:String): Resource<List<Travel>>
}