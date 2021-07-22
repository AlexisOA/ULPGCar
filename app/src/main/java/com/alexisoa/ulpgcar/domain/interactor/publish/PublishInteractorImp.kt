package com.alexisoa.ulpgcar.domain.interactor.publish

import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.repository.publish.PublishRepository
import com.alexisoa.ulpgcar.valueobject.Resource

class PublishInteractorImp(private val repository: PublishRepository): PublishInteractor {
    override suspend fun getDataAnnouncement(data : Travel): Resource<Boolean> {
        return repository.saveAnnouncement(data)
    }

    override suspend fun updateAnnouncement(data: Travel): Resource<Boolean> {
        return repository.updateAnnouncement(data)
    }


}