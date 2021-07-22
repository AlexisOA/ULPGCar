package com.alexisoa.ulpgcar.data.repository.request

import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.valueobject.Resource

interface RequestRepository {
    suspend fun getAllRequestFromRepo(travelId: String) : Resource<List<Request>>
    suspend fun setRequestRejectFromRepo(request: Request) : Resource<List<Request>>
    suspend fun setRequestAcceptFromRepo(request: Request) : Resource<List<Request>>

}