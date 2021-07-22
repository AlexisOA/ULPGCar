package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Message(
    val sender : String ="",
    val message : String = "",
    val date : Date = Date(),
    val status : String = "Sended"

) : Parcelable