package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Review(val description : String = "",
                  val dateReview: Long = 0,
                  val uidOwner: String = "",
                  val uidUser: String = "",
                  val imageUser: String = "",
                  val nameUser: String = "",
                  val nameTravelOrigin: String = "",
                  val nameTravelDestination: String = ""):Parcelable