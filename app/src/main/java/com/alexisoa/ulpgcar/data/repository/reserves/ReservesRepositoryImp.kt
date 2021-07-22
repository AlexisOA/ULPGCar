package com.alexisoa.ulpgcar.data.repository.reserves

import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.tasks.await
import java.util.*

class ReservesRepositoryImp: ReservesRepository {
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()
    
    override suspend fun setReserveInRepo(travel: Travel): Resource<Boolean> {
        if(createReserveIfDoesNotExist(travel)){
            return Resource.Success(true)
        }else{
            throw Exception("Ya ha realizado una solicitud de reserva en este anuncio")
        }

    }

    override suspend fun getReservesFromRepo(): Resource<ArrayList<Reserve>> {
        val userRef = fsDatabase.collection("users")
        val userUid = fsAuth.currentUser.uid
        val query = userRef.document(userUid).collection("reserves").get().await()
        var list = ArrayList<Reserve>()
        for (document in query.documents) {
            list.add(document.toObject(Reserve::class.java)!!)
        }

        return Resource.Success(list)
    }

    override suspend fun deleteReserveFromRepo(reserve: Reserve): Resource<ArrayList<Reserve>> {
        fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("reserves").document(reserve.travelId).delete().await()
        val refReq = fsDatabase.collection("users").document(reserve.uidOwnerReserve).collection("advertisement").document(reserve.travelId).collection("requests").document(fsAuth.currentUser.uid).get().await()
        if (refReq.exists()){
            fsDatabase.collection("users").document(reserve.uidOwnerReserve).collection("advertisement").document(reserve.travelId).collection("requests").document(fsAuth.currentUser.uid).delete().await()
        }
        return getReservesFromRepo()
    }

    override suspend fun cancelReserveFromRepo(reserve: Reserve): Resource<Boolean> {
        fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("reserves").document(reserve.travelId).update("statusReserve", "Cancelada").await()
        val refReq = fsDatabase.collection("users").document(reserve.uidOwnerReserve).collection("advertisement").document(reserve.travelId).collection("requests").document(fsAuth.currentUser.uid).get().await()
        if (refReq.exists()){
            fsDatabase.collection("users").document(reserve.uidOwnerReserve).collection("advertisement").document(reserve.travelId).collection("requests").document(fsAuth.currentUser.uid).delete().await()
        }
        return Resource.Success(true)
    }

    suspend fun createReserveIfDoesNotExist(travel: Travel):Boolean {
        print("")
        var flag = false
        val mapReserve = HashMap<String, Any>()
        val arrayUidUser = mapOf(
                "uidPassenger" to fsAuth.currentUser.uid,
                "fullName" to prefs.getName(),
                "travelId" to travel.travelId,
                "statusReserve" to "Pendiente",
                "dateReserve" to Date().time,
                "profileImage" to prefs.getUrlImage()
        )
        mapReserve["requests"] = Arrays.asList(arrayUidUser)
        //En este mapa falta la imagen del que publica el anuncio
        val refUser = fsDatabase.collection("users").document(travel.userId).get().await()
        val dataReserves = mapOf(
                "uidOwnerReserve" to travel.userId,
                "travelId" to travel.travelId,
                "dateReserve" to Date().time,
                "nameOrigin" to travel.nameOrigin,
                "nameDestination" to travel.nameDestination,
                "dateTime" to travel.dateTime,
                "timeFinish" to travel.timeFinish,
                "passengers" to travel.passengers,
                "cost" to travel.cost,
                "brandCar" to travel.modelVehicle,
                "plateCar" to travel.plateVehicle,
                "fullName" to travel.fullname,
                "statusReserve" to "Pendiente",
                "profileImage" to refUser.get("imageUrl"),
                "finished" to travel.finished
        )
        val reserveUser = fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("reserves").document(travel.travelId).get().await()
        if (!reserveUser.exists()){
            fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("reserves").document(travel.travelId).set(dataReserves).await()
            fsDatabase.collection("users").document(travel.userId).collection("advertisement").document(travel.travelId).collection("requests").document(fsAuth.currentUser.uid).set(arrayUidUser).await()

            flag = true
        }
        return flag
    }

    override suspend fun onChangedReserveSnapshot(onChangedReserve: (r: Reserve) -> Unit)  : Resource<Boolean> {
        fsDatabase.collection("users").document(prefs.getUID()).collection("reserves").addSnapshotListener(MetadataChanges.INCLUDE){ chats, error ->
            if (error == null){
                for (doc in chats!!.documentChanges){
                    val size = chats.documentChanges.size
                    if (size <= 2 && doc.type == DocumentChange.Type.MODIFIED){
                        println("Se modificó una reserva")
                        val reserve = doc.document.toObject(Reserve::class.java)
                        onChangedReserve(reserve)
                    }

                }
            }
        }

        return Resource.Success(true)
    }

    override suspend fun deleteReserveFinishedRepo(reserve: Reserve): Resource<Boolean> {
        fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("reserves").document(reserve.travelId).delete().await()
        val refReq = fsDatabase.collection("users").document(reserve.uidOwnerReserve).collection("advertisement").document(reserve.travelId).collection("requests").document(fsAuth.currentUser.uid).get().await()
        if (refReq.exists()){
            fsDatabase.collection("users").document(reserve.uidOwnerReserve).collection("advertisement").document(reserve.travelId).collection("requests").document(fsAuth.currentUser.uid).delete().await()
        }
        return Resource.Success(true)
    }
}