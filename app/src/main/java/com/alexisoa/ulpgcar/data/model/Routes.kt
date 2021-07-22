package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class  Routes(
    @SerializedName("coordinates")
    val coordinates : List<List<Double>>) : Parcelable


@Parcelize
data class Features(
    @SerializedName("geometry")
    val routes : Routes
): Parcelable

@Parcelize
data class FeaturesList(
    @SerializedName("features")
    val features : List<Features>
): Parcelable
