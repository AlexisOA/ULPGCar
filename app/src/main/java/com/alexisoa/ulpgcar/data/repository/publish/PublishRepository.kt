package com.alexisoa.ulpgcar.data.repository.publish

import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource

interface PublishRepository {
    suspend fun saveAnnouncement(data: Travel): Resource<Boolean>

    suspend fun updateAnnouncement(data: Travel): Resource<Boolean>
}