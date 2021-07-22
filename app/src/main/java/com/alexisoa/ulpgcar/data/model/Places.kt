package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Places (
        @SerializedName("display_name")
        val display_name: String = "",
        @SerializedName("address")
         val address: Address,
        @SerializedName("lat")
        val latitude : String,
        @SerializedName("lon")
        val longitude : String
): Parcelable


@Parcelize
data class Address(
        var city: String
): Parcelable