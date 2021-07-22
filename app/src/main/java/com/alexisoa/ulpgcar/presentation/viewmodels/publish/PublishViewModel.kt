package com.alexisoa.ulpgcar.presentation.viewmodels.publish

import androidx.lifecycle.*
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractor
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class PublishViewModel(val vehiclesInteractor: VehiclesInteractor, val publishInteractor: PublishInteractor): ViewModel(){

    fun getDataVehicles() = liveData<Resource<List<Vehicle>>>(Dispatchers.IO) {
        println("En el viewModel")
        emit(Resource.Loading())
        try {
            emit(vehiclesInteractor.getLisVehicles())
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun createAnnouncement(data: Travel)
            = liveData<Resource<Boolean>>(Dispatchers.IO) {
        println("En el viewModel")
        emit(Resource.Loading())
        try {
            emit(publishInteractor.getDataAnnouncement(data))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun updateAnnouncement(data: Travel)
            = liveData<Resource<Boolean>>(Dispatchers.IO) {
        println("En el viewModel")
        emit(Resource.Loading())
        try {
            emit(publishInteractor.updateAnnouncement(data))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }


}