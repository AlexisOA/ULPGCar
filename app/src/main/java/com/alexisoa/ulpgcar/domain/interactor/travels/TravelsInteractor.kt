package com.alexisoa.ulpgcar.domain.interactor.travels

import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource
import com.firebase.ui.firestore.FirestoreRecyclerOptions

interface TravelsInteractor {
    suspend fun getTravelsInteractor(): Resource<List<Travel>>
    suspend fun deleteTravel(travel: Travel) : Resource<Boolean>
    suspend fun setTravelFinishInteractor(travel: Travel)  : Resource<Boolean>
    suspend fun deleteTravelAfterNotiGroup(travel: Travel) : Resource<Boolean>
}