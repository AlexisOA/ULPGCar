package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vehicle(
                    @SerializedName("make_id")
                    val makeId: String = "",
                    @SerializedName("make")
                    val makeName: String = "") : Parcelable

data class VehiclesList(
        @SerializedName("result")
        val vehiclesList:List<Vehicle>
)