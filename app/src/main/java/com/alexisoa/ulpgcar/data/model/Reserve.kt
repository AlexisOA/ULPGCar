package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reserve(
        val uidOwnerReserve : String = "",
        val travelId : String = "",
        val dateReserve : Long = 0,
        val nameOrigin: String = "",
        val nameDestination : String = "",
        val dateTime : Long = 0,
        val timeFinish : Long = 0,
        val passengers : Int = 0,
        val cost : String = "",
        val brandCar : String = "",
        val plateCar : String = "",
        val fullName : String = "",
        val statusReserve: String = "",
        val profileImage : String = "",
        val finished : Boolean = false



): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reserve

        if (uidOwnerReserve != other.uidOwnerReserve) return false

        return true
    }

    override fun hashCode(): Int {
        return uidOwnerReserve.hashCode()
    }
}