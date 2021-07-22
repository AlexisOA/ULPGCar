package com.alexisoa.ulpgcar.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class User(val fullName: String = "",
                val email: String = "",
                val uid: String = "",
                val imageUrl: String = "",
                val provider: String = "",
                val dateRegister: Long = 0,
                val appToken : String = "",
                val description: String = ""
                ): Parcelable