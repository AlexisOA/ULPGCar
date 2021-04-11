package com.example.ulpgcarprototype.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (val name: String = "default name",
                 val provider: String = "default"): Parcelable{
}