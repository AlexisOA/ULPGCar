package com.alexisoa.ulpgcar.domain.interactor.travels

import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepository
import com.alexisoa.ulpgcar.valueobject.Resource
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class TravelsInteractorImp(private val repository: TravelsRepository):TravelsInteractor {
    override suspend fun getTravelsInteractor() :Resource<List<Travel>>{
        return repository.getTravelFromRepo()
    }

    override suspend fun deleteTravel(travel: Travel): Resource<Boolean> {
        return repository.deleteTravelRepo(travel)
    }

    override suspend fun setTravelFinishInteractor(travel: Travel): Resource<Boolean> {
        return repository.setTravelFinishRepo(travel)
    }

    override suspend fun deleteTravelAfterNotiGroup(travel: Travel): Resource<Boolean> {
        return repository.deleteTravelAfterNotiGroup(travel)
    }
}