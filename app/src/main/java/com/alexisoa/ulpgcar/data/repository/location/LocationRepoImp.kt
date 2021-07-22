package com.alexisoa.ulpgcar.data.repository.location

import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.valueobject.Resource

class LocationRepoImp(private val dataSource: DataPlacesRepo) : LocationRepo {
    override suspend fun getLocationList(nameLocation: String): Resource<ArrayList<Places>> {
        return dataSource.getLocationByName(nameLocation)
    }


}