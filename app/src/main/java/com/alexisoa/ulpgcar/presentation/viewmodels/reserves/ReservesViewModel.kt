package com.alexisoa.ulpgcar.presentation.viewmodels.reserves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.domain.interactor.reserves.ReservesInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.util.ArrayList

class ReservesViewModel(val interactor : ReservesInteractor): ViewModel()  {


    fun reserveTravel(travel : Travel) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.reserveTravel(travel))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun getAllReserves() = liveData<Resource<ArrayList<Reserve>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.getReservesInteractor())
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun deleteReserve(reserve: Reserve) = liveData<Resource<ArrayList<Reserve>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.deleteReserveInteractor(reserve))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun deleteReserveFinished(reserve: Reserve) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.deleteReserveFinishedInteractor(reserve))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun cancelTravel(reserve: Reserve) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.cancelTravelInteractor(reserve))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun onChangedReserveSnapshot(onChangedReserve: (r: Reserve) -> Unit) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.onChangedReserveSnapshot(onChangedReserve))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }


}