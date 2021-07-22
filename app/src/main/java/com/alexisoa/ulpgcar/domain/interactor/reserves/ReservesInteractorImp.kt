package com.alexisoa.ulpgcar.domain.interactor.reserves

import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.repository.reserves.ReservesRepository
import com.alexisoa.ulpgcar.valueobject.Resource
import java.util.ArrayList

class ReservesInteractorImp(private val repository: ReservesRepository): ReservesInteractor {
    override suspend fun reserveTravel(travel: Travel): Resource<Boolean> {
        return repository.setReserveInRepo(travel)
    }

    override suspend fun getReservesInteractor(): Resource<ArrayList<Reserve>> {
        return repository.getReservesFromRepo()
    }

    override suspend fun deleteReserveInteractor(reserve: Reserve): Resource<ArrayList<Reserve>> {
        return repository.deleteReserveFromRepo(reserve)
    }

    override suspend fun cancelTravelInteractor(reserve: Reserve): Resource<Boolean> {
        return repository.cancelReserveFromRepo(reserve)
    }

    override suspend fun onChangedReserveSnapshot(onChangedReserve: (r: Reserve) -> Unit): Resource<Boolean> {
        return repository.onChangedReserveSnapshot(onChangedReserve)
    }

    override suspend fun deleteReserveFinishedInteractor(reserve: Reserve): Resource<Boolean> {
        return repository.deleteReserveFinishedRepo(reserve)
    }


}