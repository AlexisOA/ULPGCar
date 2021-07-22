package com.alexisoa.ulpgcar.domain.interactor.reserves

import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource
import java.util.ArrayList

interface ReservesInteractor {
    suspend fun reserveTravel(travel: Travel): Resource<Boolean>
    suspend fun getReservesInteractor() : Resource<ArrayList<Reserve>>
    suspend fun deleteReserveInteractor(reserve: Reserve) : Resource<ArrayList<Reserve>>
    suspend fun cancelTravelInteractor(reserve: Reserve): Resource<Boolean>
    suspend fun onChangedReserveSnapshot(onChangedReserve: (r: Reserve) -> Unit)  : Resource<Boolean>
    suspend fun deleteReserveFinishedInteractor(reserve: Reserve) : Resource<Boolean>
}