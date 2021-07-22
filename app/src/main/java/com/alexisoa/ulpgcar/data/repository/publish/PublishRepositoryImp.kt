package com.alexisoa.ulpgcar.data.repository.publish

import com.alexisoa.ulpgcar.data.model.*
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PublishRepositoryImp: PublishRepository {
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()



    override suspend fun saveAnnouncement(data : Travel): Resource<Boolean> {
        val userRef = fsDatabase.collection("users")
        val userUid = fsAuth.currentUser.uid

        val x = getBoundBox(data)

        val fullname = prefs.getName()
        val announcementData = mutableMapOf(
                "long_init" to data.long_init,
                "lat_init" to data.lat_init,
                "long_dest" to data.long_dest,
                "lat_dest" to data.lat_dest,
                "dateTime" to data.dateTime,
                "timeFinish" to data.timeFinish,
                "passengers" to data.passengers,
                "modelVehicle" to data.modelVehicle,
                "plateVehicle" to data.plateVehicle,
                "fullname" to fullname,
                "cost" to data.cost,
                "nameOrigin" to data.nameOrigin,
                "nameDestination" to data.nameDestination,
                "smokePermission" to data.smokePermission,
                "eatPermission" to data.eatPermission,
                "musicPermission" to data.musicPermission,
                "userId" to userUid,
                "travelId" to "",
                "profileImage" to prefs.getUrlImage(),
                "routePoints" to data.routePoints,
                "boundBox" to x,
                "finished" to false
        )
        /*val travelsRef = fsDatabase.collection("travels")
        val travelDocument = travelsRef.add(announcementData).await()
        travelsRef.document(travelDocument.id).update("travelId", travelDocument.id).await()*/
        
        val advertisementRef = userRef.document(userUid).collection("advertisement")
        val advertisementRefDocument = advertisementRef.add(announcementData).await()
        advertisementRef.document(advertisementRefDocument.id).update("travelId", advertisementRefDocument.id).await()


        val travelsRef = fsDatabase.collection("travels")
        travelsRef.document(advertisementRefDocument.id).set(announcementData).await()
        //val travelDocument = travelsRef.add(announcementData).await()
        travelsRef.document(advertisementRefDocument.id).update("travelId", advertisementRefDocument.id).await()

        return Resource.Success(true)

    }

    private fun getBoundBox(data: Travel): RectangleLatLng {
        var minX = 181.0
        var minY = 91.0
        var maxX = -181.0
        var maxY = -91.0

        for (point in data.routePoints!!) {
            minX = min(minX, point.longitude)
            minY = min(minY, point.latitude)

            maxX = max(maxX, point.longitude)
            maxY = max(maxY, point.latitude)
        }

        return RectangleLatLng(LatLng(minY, minX), LatLng(maxY, maxX))
    }

    override suspend fun updateAnnouncement(data: Travel): Resource<Boolean>{
        val userRef = fsDatabase.collection("users")
        val userUid = fsAuth.currentUser.uid
        val fullname = prefs.getName()
        val x = getBoundBox(data)

        val announcementData = mapOf(
                "long_init" to data.long_init,
                "lat_init" to data.lat_init,
                "long_dest" to data.long_dest,
                "lat_dest" to data.lat_dest,
                "dateTime" to data.dateTime,
                "timeFinish" to data.timeFinish,
                "passengers" to data.passengers,
                "modelVehicle" to data.modelVehicle,
                "plateVehicle" to data.plateVehicle,
                "fullname" to fullname,
                "cost" to data.cost,
                "nameOrigin" to data.nameOrigin,
                "nameDestination" to data.nameDestination,
                "smokePermission" to data.smokePermission,
                "eatPermission" to data.eatPermission,
                "musicPermission" to data.musicPermission,
                "userId" to userUid,
                "travelId" to data.travelId,
                "profileImage" to prefs.getUrlImage(),
                "routePoints" to data.routePoints,
                "boundBox" to x,
                "finished" to false
        )
        userRef.document(userUid).collection("advertisement").document(data.travelId).update(announcementData).await()
        fsDatabase.collection("travels").document(data.travelId).update(announcementData).await()
        return Resource.Success(true)

    }


}