package com.alexisoa.ulpgcar.data.repository.reserves

import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource
import java.util.ArrayList

interface ReservesRepository {
    suspend fun setReserveInRepo(travel: Travel): Resource<Boolean>
    suspend fun getReservesFromRepo() : Resource<ArrayList<Reserve>>
    suspend fun deleteReserveFromRepo(reserve: Reserve) : Resource<ArrayList<Reserve>>
    suspend fun cancelReserveFromRepo(reserve: Reserve): Resource<Boolean>
    suspend fun onChangedReserveSnapshot(onChangedReserve: (r: Reserve) -> Unit)  : Resource<Boolean>
    suspend fun deleteReserveFinishedRepo(reserve: Reserve): Resource<Boolean>
}
