package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat(val receiver : String = "",
                val fullName: String = "",
                val profileImage : String = ""): Parcelable