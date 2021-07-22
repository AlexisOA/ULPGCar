package com.alexisoa.ulpgcar.presentation.viewmodels.routes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.Features
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.domain.interactor.routes.RoutesInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class RoutesViewModel(val interactor: RoutesInteractor):ViewModel() {

    fun getDataRoutes(coordinatesInit:LatLng, coordinatesFin: LatLng) = liveData<Resource<List<Features>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.getListRoutes(coordinatesInit, coordinatesFin))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }
}