package com.alexisoa.ulpgcar.domain.interactor.request

import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.repository.request.RequestRepository
import com.alexisoa.ulpgcar.valueobject.Resource

class RequestInteractorImp(private val repository: RequestRepository):RequestInteractor {
    override suspend fun getAllRequestInteractor(travelId: String): Resource<List<Request>> {
        return repository.getAllRequestFromRepo(travelId)
    }

    override suspend fun setRequestRejectInteractor(request: Request): Resource<List<Request>> {
        return repository.setRequestRejectFromRepo(request)
    }

    override suspend fun setRequestAcceptInteractor(request: Request): Resource<List<Request>> {
        return repository.setRequestAcceptFromRepo(request)
    }
}