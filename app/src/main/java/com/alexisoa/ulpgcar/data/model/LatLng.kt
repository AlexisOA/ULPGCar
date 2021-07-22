package com.alexisoa.ulpgcar.data.model

import android.location.Location
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


fun min(a : Double, b : Double) : Double {
    if (a < b) {
        return a
    }
    return b
}

fun max(a : Double, b : Double) : Double {
    if (a > b) {
        return a
    }
    return b
}

@Parcelize
class LatLng(var latitude : Double = 0.0,
                var longitude : Double = 0.0):Parcelable {

    fun distanceTo(point : LatLng) : Float {

        val loc1 = Location("")
        loc1.setLatitude(point.latitude)
        loc1.setLongitude(point.longitude)

        val loc2 = Location("")
        loc2.setLatitude(latitude)
        loc2.setLongitude(longitude)

        return loc1.distanceTo(loc2)
    }
}

@Parcelize
class RectangleLatLng(var min: LatLng? = null,
                      var max: LatLng? = null):Parcelable {

    private fun upLeft() : LatLng {
        return LatLng(min!!.longitude, max!!.latitude)
    }

    private fun downRight() : LatLng {
        return LatLng(max!!.longitude, min!!.latitude)
    }

    fun padding(padding : Double) : RectangleLatLng {
        return RectangleLatLng(LatLng(min!!.latitude-padding, min!!.longitude-padding),
                LatLng(max!!.latitude+padding, max!!.longitude+padding))
    }

    fun contains(point : LatLng) : Boolean {
        return min!!.longitude <= point.longitude
                &&  min!!.latitude <= point.latitude
                &&  max!!.longitude >= point.longitude
                && max!!.latitude >= point.latitude
    }

    fun contains(rect : RectangleLatLng) : Boolean {
        return contains(rect.min!!) && contains(rect.max!!)
    }


    override fun toString() : String {
        return "Min: [" + min!!.longitude + " " + min!!.latitude + "], [" + max!!.longitude + " " + max!!.latitude + "]"
    }
}