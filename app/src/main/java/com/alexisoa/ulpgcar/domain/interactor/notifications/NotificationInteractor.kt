package com.alexisoa.ulpgcar.domain.interactor.notifications

import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.RequestQueue

interface NotificationInteractor {
    suspend fun sendNotification(title:String, detail:String, myRequest: RequestQueue, userUid:String): Resource<Boolean>
    suspend fun sendNotificationGroup(title:String, detail:String, myRequest: RequestQueue, travelId:String): Resource<Boolean>
}