package com.alexisoa.ulpgcar.data.repository.notifications

import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.RequestQueue

interface NotificationRepository {
    suspend fun sendNotificationFromRepo(title:String, detail:String, myRequest: RequestQueue, userUid:String): Resource<Boolean>
    suspend fun sendNotificationGroupFromRepo(title:String, detail:String, myRequest: RequestQueue, travelId:String): Resource<Boolean>
}