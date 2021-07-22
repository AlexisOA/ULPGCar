package com.alexisoa.ulpgcar.presentation.viewmodels.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.domain.interactor.request.RequestInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class RequestViewModel (val interactor : RequestInteractor): ViewModel(){
    fun getAllRequests(travelId: String) = liveData<Resource<List<Request>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.getAllRequestInteractor(travelId))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun setRequestReject(request: Request) = liveData<Resource<List<Request>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.setRequestRejectInteractor(request))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }


    fun setRequestAccept(request: Request) = liveData<Resource<List<Request>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.setRequestAcceptInteractor(request))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

}

