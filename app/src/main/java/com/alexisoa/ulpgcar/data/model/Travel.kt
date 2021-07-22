package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Travel(
        val lat_init: Double = 0.0,
        val long_init: Double = 0.0,
        val lat_dest: Double = 0.0,
        val long_dest: Double = 0.0,
        val nameOrigin: String = "",
        val nameDestination: String = "",
        val dateTime: Long = 0,
        val timeFinish: Long = 0,
        val passengers: Int = 0,
        val plateVehicle: String = "",
        val modelVehicle: String = "",
        val cost: String = "",
        val eatPermission: String = "",
        val smokePermission: String = "",
        val musicPermission: String = "",
        val userId: String = "",
        val fullname: String = "",
        val travelId: String = "",
        val profileImage: String = "",
        var routePoints: ArrayList<LatLng>? = null,
        var boundBox: RectangleLatLng? = null,
        val finished : Boolean = false
): Parcelable