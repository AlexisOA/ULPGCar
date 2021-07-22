package com.alexisoa.ulpgcar.data.repository.location

import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.valueobject.Resource
import com.alexisoa.ulpgcar.valueobject.RetrofitClient

class DataPlacesImp : DataPlacesRepo {

    override suspend fun getLocationByName(nameLocation: String): Resource<ArrayList<Places>> {
        return Resource.Success(RetrofitClient.webserviceStreet.getLocationByName(nameLocation.replace(' ', '+')))
    }
}