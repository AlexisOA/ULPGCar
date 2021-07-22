package com.alexisoa.ulpgcar.domain.interactor.publish

import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource

interface PublishInteractor {
    suspend fun getDataAnnouncement(data: Travel): Resource<Boolean>

    suspend fun updateAnnouncement(data: Travel): Resource<Boolean>
}