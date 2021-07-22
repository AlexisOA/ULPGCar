package com.alexisoa.ulpgcar.data.repository.request

import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.model.RequestDocument
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class RequestRepositoryImp : RequestRepository {
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()
    override suspend fun getAllRequestFromRepo(travelId: String): Resource<List<Request>> {
        //val requests = getListRequest(travelId)
        var requests = getListRequest(travelId)
        return Resource.Success(requests)
    }

    suspend fun getListRequest(travelId: String): ArrayList<Request> {
        val userRef = fsDatabase.collection("users")
        val query = userRef.document(fsAuth.currentUser.uid).collection("advertisement").document(travelId).collection("requests").get().await()
        var requests = ArrayList<Request>()
        for (document in query.documents) {
            requests.add(document.toObject(Request::class.java)!!)
        }
        return requests
    }


    override suspend fun setRequestRejectFromRepo(request: Request): Resource<List<Request>> {
        val arrayUidUser = mapOf(
                "dateReserve" to request.dateReserve,
                "fullName" to request.fullName,
                "statusReserve" to request.statusReserve,
                "travelId" to request.travelId,
                "uidPassenger" to request.uidPassenger
                )

        fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("advertisement").document(request.travelId).collection("requests").document(request.uidPassenger).delete()
        fsDatabase.collection("users").document(request.uidPassenger).collection("reserves").document(request.travelId).update("statusReserve", "Rechazada").await()
        //val requests = getListRequest(request.travelId)
        var requests = getListRequest(request.travelId)
        return Resource.Success(requests)
    }

    override suspend fun setRequestAcceptFromRepo(request: Request): Resource<List<Request>> {

        fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("advertisement").document(request.travelId).collection("requests").document(request.uidPassenger).update("statusReserve", "Aceptada").await()
        fsDatabase.collection("users").document(request.uidPassenger).collection("reserves").document(request.travelId).update("statusReserve", "Aceptada").await()
        //val requests = getListRequest(request.travelId)
        var requests = getListRequest(request.travelId)
        return Resource.Success(requests)
    }
}