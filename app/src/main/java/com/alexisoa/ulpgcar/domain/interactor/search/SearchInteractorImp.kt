package com.alexisoa.ulpgcar.domain.interactor.search

import android.location.Location
import com.alexisoa.ulpgcar.data.model.*
import com.alexisoa.ulpgcar.data.repository.location.LocationRepo
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepository
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.Resource

class SearchInteractorImp(private val repository : LocationRepo, private val repoTravel: TravelsRepository) : SearchInteractor {

    override suspend fun getListLocations(text: String) : Resource<ArrayList<Places>> {
        val name = "search?q=$text&format=json&addressdetails=1&countrycodes=es"
        return repository.getLocationList(name)
    }



     override suspend fun getTravelsMatch(latitudeDest : Double, longitudeDest: Double,
                                          latitudeInit : Double, longitudeInit: Double,
                                          searchPolilyne: ArrayList<LatLng>,
                                          date:Long, passengers:String): Resource<List<Travel>>{
         val startPosition = LatLng(latitudeInit, longitudeInit)
         val endPosition = LatLng(latitudeDest, longitudeDest)

         var minX = 181.0
         var minY = 91.0
         var maxX = -181.0
         var maxY = -91.0

         for (point in searchPolilyne) {
             minX = min(minX, point.longitude)
             minY = min(minY, point.latitude)

             maxX = max(maxX, point.longitude)
             maxY = max(maxY, point.latitude)
         }

         val rect = RectangleLatLng(LatLng(minY, minX), LatLng(maxY, maxX))

         val travelsSearch = ArrayList<Travel>()
         val allTravels =  repoTravel.getAllTravels()
         for (travel in allTravels){
             if (!travel.userId.equals(prefs.getUID()) && (travel.dateTime > date && travel.dateTime < (date+86400000)) && travel.passengers >= passengers.toInt()){
                 println(travel.nameOrigin)
                 val bbox = travel.boundBox!!.padding(0.005)
                 if (bbox.contains(rect)) {
                     var containsStart = false
                     var containsEnd = false
                     for (coord in travel.routePoints!!) {
                         if (!containsStart)
                            containsStart = coord.distanceTo(startPosition) < 6000.0
                         if (!containsEnd)
                             containsEnd = coord.distanceTo(endPosition) < 6000.0
                     }

                     if (containsStart &&  containsEnd) {
                         travelsSearch.add(travel)
                     }
                 }
             }
        }
        return Resource.Success(travelsSearch)
    }



}