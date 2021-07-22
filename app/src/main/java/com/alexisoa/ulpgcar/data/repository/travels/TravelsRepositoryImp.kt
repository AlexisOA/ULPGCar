package com.alexisoa.ulpgcar.data.repository.travels

import com.alexisoa.ulpgcar.data.model.*
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TravelsRepositoryImp: TravelsRepository {
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()

    override suspend fun getTravelFromRepo() : Resource<List<Travel>>{
        val userRef = fsDatabase.collection("users")
        val userUid = fsAuth.currentUser.uid
        val query = userRef.document(userUid).collection("advertisement").get().await()
        var list = ArrayList<Travel>()
        for (document in query.documents) {
            val travel = document.toObject(Travel::class.java)!!
            var minX = 181.0
            var minY = 91.0
            var maxX = -181.0
            var maxY = -91.0

            for (point in travel.routePoints!!) {
                minX = min(minX, point.longitude)
                minY = min(minY, point.latitude)

                maxX = max(maxX, point.longitude)
                maxY = max(maxY, point.latitude)
            }

            val x = RectangleLatLng(LatLng(minY, minX), LatLng(maxY, maxX))
            val s = x.toString()

            list.add(travel)
        }

        return Resource.Success(list)

    }

    override suspend fun deleteTravelRepo(travel: Travel): Resource<Boolean> {
        val userRef = fsDatabase.collection("users")
        val userUid = fsAuth.currentUser.uid

        val query = userRef.document(prefs.getUID()).collection("advertisement").document(travel.travelId).collection("requests").get().await()
        for (document in query.documents){
            val reserveUser = userRef.document(document.id).collection("reserves").document(travel.travelId).get().await()
            if (reserveUser.exists()){
                val reserve = userRef.document(document.id).collection("reserves").document(travel.travelId).get().await()
                val status = reserve.get("statusReserve")
                if (travel.finished && !status!!.equals("Rechazada")){
                    userRef.document(document.id).collection("reserves").document(travel.travelId).update("statusReserve", "Finalizado").await()
                }else{
                    userRef.document(document.id).collection("reserves").document(travel.travelId).update("statusReserve", "Rechazada").await()
                }
            }
        }
        if (travel.finished){
            val requests = userRef.document(userUid).collection("advertisement").document(travel.travelId).collection("requests").get().await()
            for (doc in requests.documents){
                userRef.document(userUid).collection("advertisement").document(travel.travelId).collection("requests").document(doc.id).delete().await()
            }
            userRef.document(userUid).collection("advertisement").document(travel.travelId).delete().await()
        }
        val travelRef = fsDatabase.collection("travels").document(travel.travelId).get().await()
        if (travelRef.exists()){
            fsDatabase.collection("travels").document(travel.travelId).delete().await()
        }
        return Resource.Success(true)
    }

    override suspend fun getAllTravels(): ArrayList<Travel> {
        val query = fsDatabase.collection("travels").get().await()
        var list = ArrayList<Travel>()
        for (document in query.documents){
            list.add(document.toObject(Travel::class.java)!!)
        }
        return list
    }

    override suspend fun setTravelFinishRepo(travel: Travel): Resource<Boolean> {
        fsDatabase.collection("travels").document(travel.travelId).delete().await()
        val userRef = fsDatabase.collection("users")
        userRef.document(prefs.getUID()).collection("advertisement").document(travel.travelId).update("finished", true).await()
        val queryAccept = userRef.document(prefs.getUID()).collection("advertisement").document(travel.travelId).collection("requests").whereEqualTo("statusReserve", "Aceptada").get().await()
        for (document in queryAccept.documents){
            userRef.document(document.id).collection("reserves").document(travel.travelId).update("finished", true).await()
            userRef.document(document.id).collection("reserves").document(travel.travelId).update("statusReserve", "Finalizado").await()
        }
        val queryPending = userRef.document(prefs.getUID()).collection("advertisement").document(travel.travelId).collection("requests").whereEqualTo("statusReserve", "Pendiente").get().await()
        for (document in queryPending.documents){
            userRef.document(document.id).collection("reserves").document(travel.travelId).update("statusReserve", "Rechazada").await()
        }
        return Resource.Success(true)
    }

    override suspend fun deleteTravelAfterNotiGroup(travel: Travel): Resource<Boolean> {
        val userRef = fsDatabase.collection("users")
        val userUid = fsAuth.currentUser.uid
        val requests = userRef.document(userUid).collection("advertisement").document(travel.travelId).collection("requests").get().await()
        for (doc in requests.documents){
            userRef.document(userUid).collection("advertisement").document(travel.travelId).collection("requests").document(doc.id).delete().await()
        }
        userRef.document(userUid).collection("advertisement").document(travel.travelId).delete().await()
        return Resource.Success(true)
    }
}