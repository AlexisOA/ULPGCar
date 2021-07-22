package com.alexisoa.ulpgcar.presentation.viewmodels.travels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.domain.interactor.travels.TravelsInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class TravelsViewModel(val interactor : TravelsInteractor): ViewModel() {

    fun getAllTravels() = liveData<Resource<List<Travel>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.getTravelsInteractor())
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun deleteTravel(travel: Travel) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.deleteTravel(travel))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun deleteTravelAfterNotiGroup(travel: Travel) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.deleteTravelAfterNotiGroup(travel))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }


    fun setTravelFinished(travel : Travel) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.setTravelFinishInteractor(travel))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

}