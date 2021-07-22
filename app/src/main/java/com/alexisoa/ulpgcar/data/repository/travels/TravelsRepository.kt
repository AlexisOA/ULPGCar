package com.alexisoa.ulpgcar.data.repository.travels

import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource
import com.firebase.ui.firestore.FirestoreRecyclerOptions

interface TravelsRepository {
    suspend fun getTravelFromRepo() : Resource<List<Travel>>
    suspend fun deleteTravelRepo(travel: Travel) : Resource<Boolean>
    suspend fun getAllTravels(): ArrayList<Travel>
    suspend fun setTravelFinishRepo(travel: Travel) : Resource<Boolean>
    suspend fun deleteTravelAfterNotiGroup(travel: Travel) : Resource<Boolean>
}