package com.alexisoa.ulpgcar.domain.interactor.notifications

import com.alexisoa.ulpgcar.data.repository.notifications.NotificationRepository
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.RequestQueue

class NotificationInteractorImp(private val repository: NotificationRepository):NotificationInteractor {
    override suspend fun sendNotification(title:String, detail:String, myRequest: RequestQueue, userUid:String): Resource<Boolean> {
        return repository.sendNotificationFromRepo(title, detail, myRequest, userUid)
    }

    override suspend fun sendNotificationGroup(title: String, detail: String, myRequest: RequestQueue, travelId: String): Resource<Boolean> {
        return repository.sendNotificationGroupFromRepo(title, detail, myRequest, travelId)
    }
}