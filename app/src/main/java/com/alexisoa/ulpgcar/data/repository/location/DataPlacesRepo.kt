package com.alexisoa.ulpgcar.data.repository.location

import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.valueobject.Resource

interface DataPlacesRepo {
    suspend fun getLocationByName(nameLocation: String): Resource<ArrayList<Places>>
}