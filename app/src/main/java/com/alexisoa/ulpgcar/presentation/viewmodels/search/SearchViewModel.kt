package com.alexisoa.ulpgcar.presentation.viewmodels.search

import androidx.lifecycle.*
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.domain.interactor.search.SearchInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class SearchViewModel (val interactorSearch: SearchInteractor): ViewModel(){

    private val locationsData = MutableLiveData<String>()

    fun setLocation(locationName : String){
        locationsData.value = locationName
    }

    fun searchLocation() = locationsData.switchMap { nameLocation ->
        liveData<Resource<ArrayList<Places>>>(Dispatchers.IO){
            emit(Resource.Loading())
            try {
                emit(interactorSearch.getListLocations(nameLocation))
            }catch (e: Exception){
                emit(Resource.Failure(e))
            }
        }
    }


    fun getTravels(latitudeDest : Double, longitudeDest: Double,
                   latitudeInit : Double, longitudeInit: Double,
                   searchPolilyne: ArrayList<com.alexisoa.ulpgcar.data.model.LatLng>, date: Long, passenger:String) = liveData<Resource<List<Travel>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactorSearch.getTravelsMatch(latitudeDest, longitudeDest, latitudeInit, longitudeInit, searchPolilyne, date, passenger))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}