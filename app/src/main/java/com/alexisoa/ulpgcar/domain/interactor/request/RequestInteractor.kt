package com.alexisoa.ulpgcar.domain.interactor.request

import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.valueobject.Resource

interface RequestInteractor {
    suspend fun getAllRequestInteractor(travelId: String) : Resource<List<Request>>
    suspend fun setRequestRejectInteractor(request: Request) : Resource<List<Request>>
    suspend fun setRequestAcceptInteractor(request: Request) : Resource<List<Request>>

}