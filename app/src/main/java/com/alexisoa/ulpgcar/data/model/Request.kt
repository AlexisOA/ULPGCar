package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Request(val uidPassenger: String = "",
                   val fullName: String = "",
                   val travelId: String = "",
                   val statusReserve: String = "",
                   val dateReserve: Long = 0,
                   val profileImage : String = ""): Parcelable