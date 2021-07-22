package com.alexisoa.ulpgcar.presentation.viewmodels.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.RequestQueue
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class NotificationViewModel(val interactor : NotificationInteractor):ViewModel() {

    fun sendNotification(title:String, detail:String, myRequest:RequestQueue, userUid:String) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.sendNotification(title, detail, myRequest, userUid))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun sendNotificationGroup(title:String, detail:String, myRequest:RequestQueue, travelId:String) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.sendNotificationGroup(title, detail, myRequest, travelId))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }
}