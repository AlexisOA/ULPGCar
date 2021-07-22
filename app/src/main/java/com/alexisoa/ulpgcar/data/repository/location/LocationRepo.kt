package com.alexisoa.ulpgcar.data.repository.location

import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.valueobject.Resource

interface LocationRepo {
    suspend fun getLocationList(nameLocation: String): Resource<ArrayList<Places>>
}