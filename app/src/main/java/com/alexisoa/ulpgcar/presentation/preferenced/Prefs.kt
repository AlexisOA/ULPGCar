package com.alexisoa.ulpgcar.presentation.preferenced

import android.content.Context
import java.util.*

class Prefs(val context: Context){
    val SHARED_NAME =  "Mydtb"
    val SHARED_USER_EMAIL = "email"
    val SHARED_USER_NAME = "name"
    val SHARED_USER_UID = "uid"
    val SHARED_USER_URLIMAGE = "url"
    val SHARED_USER_PROVIDER = "provider"
    val SHARED_USER_DATE = "date"
    val SHARED_USER_TOKEN_APP = "tokenapp"
    val SHARED_USER_DESCRIPTION = "miDescription"

    val SHARED_TRAVEL = "MyPublish"
    val SHARED_TRAVEL_LAT_ORIGIN = "lat_origin"
    val SHARED_TRAVEL_LON_ORIGIN = "lon_origin"
    val SHARED_TRAVEL_LAT_DEST = "lat_dest"
    val SHARED_TRAVEL_LONG_DEST = "lon_dest"
    val SHARED_TRAVEL_NAME_ORIGIN = "name_origin"
    val SHARED_TRAVEL_NAME_DEST = "name_dest"
    val SHARED_TRAVEL_DATE = "date"
    val SHARED_TRAVEL_HOUR = "hour start"
    val SHARED_TRAVEL_HOUR_FINISH = "hour finish"
    val SHARED_TRAVEL_PASSENGERS = "passengers"
    val SHARED_TRAVEL_VEHICLE_MODEL = "vehicle_model"
    val SHARED_TRAVEL_VEHICLE_BRAND = "vehicle_brand"
    val SHARED_TRAVEL_PREF_SMOKE = "No Se permite fumar"
    val SHARED_TRAVEL_PREF_MUSIC = "No Se permite poner música"
    val SHARED_TRAVEL_PREF_EAT = "No Se permite comer"
    val SHARED_TRAVEL_PRICE = "price"
    val SHARED_TYPE = "type"
    val SHARED_EDIT = "notEdit"
    val SHARED_EDIT_STATUS="status"
    val SHARED_JSON_ROUTE = "json"

    val SHARED_NOTIFICATIONS = "MyNotifications"
    val SHARED_NOTI_CHAT = "noChat"
    val SHARED_NOTI_RESERVES = "noReserve"
    val SHARED_NOTI_REQUEST = "noRequest"
    val SHARED_NOTI_REVIEWS = "noReviews"

    val storage = context.getSharedPreferences(SHARED_NAME, 0)
    val storagePublish = context.getSharedPreferences(SHARED_TRAVEL, 0)
    val storageNoti = context.getSharedPreferences(SHARED_NOTIFICATIONS, 0)

    fun saveNotiChat(status: String){
        storageNoti.edit().putString(SHARED_NOTI_CHAT, status).apply()
    }

    fun saveNotiReserve(status: String){
        storageNoti.edit().putString(SHARED_NOTI_RESERVES, status).apply()
    }

    fun saveNotiRequest(status: String){
        storageNoti.edit().putString(SHARED_NOTI_REQUEST, status).apply()
    }

    fun saveNotiReviews(status: String){
        storageNoti.edit().putString(SHARED_NOTI_REVIEWS, status).apply()
    }

    fun saveEmail(email: String){
        storage.edit().putString(SHARED_USER_EMAIL, email).apply()
    }

    fun saveName(name: String){
        storage.edit().putString(SHARED_USER_NAME, name).apply()
    }

    fun saveUID(uid: String){
        storage.edit().putString(SHARED_USER_UID, uid).apply()
    }

    fun saveTokenApp(token: String){
        storage.edit().putString(SHARED_USER_TOKEN_APP, token).apply()
    }

    fun saveDescription(description: String){
        storage.edit().putString(SHARED_USER_DESCRIPTION, description).apply()
    }

    fun saveUrlImage(url: String){
        storage.edit().putString(SHARED_USER_URLIMAGE, url).apply()
    }


    fun saveProvider(provider: String){
        storage.edit().putString(SHARED_USER_PROVIDER, provider).apply()
    }

    fun saveDateCreation(date: String){
        storage.edit().putString(SHARED_USER_DATE, date).apply()
    }

    fun saveLatOrigin(lat: String){
        storagePublish.edit().putString(SHARED_TRAVEL_LAT_ORIGIN, lat).apply()
    }
    fun saveLonOrigin(lon: String){
        storagePublish.edit().putString(SHARED_TRAVEL_LON_ORIGIN, lon).apply()
    }
    fun saveLatDest(lat: String){
        storagePublish.edit().putString(SHARED_TRAVEL_LAT_DEST, lat).apply()
    }
    fun saveLonDest(lon: String){
        storagePublish.edit().putString(SHARED_TRAVEL_LONG_DEST, lon).apply()
    }
    fun saveNameOrigin(name: String){
        storagePublish.edit().putString(SHARED_TRAVEL_NAME_ORIGIN, name).apply()
    }
    fun saveNameDest(name: String){
        storagePublish.edit().putString(SHARED_TRAVEL_NAME_DEST, name).apply()
    }
    fun saveDate(date: String){
        storagePublish.edit().putString(SHARED_TRAVEL_DATE, date).apply()
    }
    fun saveHourStart(hour: String){
        storagePublish.edit().putString(SHARED_TRAVEL_HOUR, hour).apply()
    }

    fun saveHourFinished(hour: String){
        storagePublish.edit().putString(SHARED_TRAVEL_HOUR_FINISH, hour).apply()
    }
    fun savePassengers(pass: String){
        storagePublish.edit().putString(SHARED_TRAVEL_PASSENGERS, pass).apply()
    }
    fun saveVehicleModel(model: String){
        storagePublish.edit().putString(SHARED_TRAVEL_VEHICLE_MODEL, model).apply()
    }
    fun saveVehicleBrand(brand: String){
        storagePublish.edit().putString(SHARED_TRAVEL_VEHICLE_BRAND, brand).apply()
    }
    fun savePrefSmoke(smoke: String){
        storagePublish.edit().putString(SHARED_TRAVEL_PREF_SMOKE, smoke).apply()
    }
    fun savePrefMusic(music: String){
        storagePublish.edit().putString(SHARED_TRAVEL_PREF_MUSIC, music).apply()
    }
    fun savePrefEat(eat: String){
        storagePublish.edit().putString(SHARED_TRAVEL_PREF_EAT, eat).apply()
    }
    fun savePrice(price: String){
        storagePublish.edit().putString(SHARED_TRAVEL_PRICE, price).apply()
    }
    fun saveType(type: String){
        storagePublish.edit().putString(SHARED_TYPE, type).apply()
    }
    fun saveEdit(edit: String){
        storagePublish.edit().putString(SHARED_EDIT, edit).apply()
    }

    fun saveEditStatus(status: String){
        storagePublish.edit().putString(SHARED_EDIT_STATUS, status).apply()
    }

    fun saveJsonRoute(json: String){
        storagePublish.edit().putString(SHARED_JSON_ROUTE, json).apply()
    }

    fun wipe(){
        storage.edit().clear().apply()
    }

    fun wipePublish(){
        storagePublish.edit().clear().apply()
    }

    fun getEmail(): String = storage.getString(SHARED_USER_EMAIL, "")!!

    fun getName(): String = storage.getString(SHARED_USER_NAME, "")!!

    fun getUID(): String = storage.getString(SHARED_USER_UID, "")!!

    fun getTokenApp(): String = storage.getString(SHARED_USER_TOKEN_APP, "")!!

    fun getUrlImage(): String = storage.getString(SHARED_USER_URLIMAGE, "")!!


    fun getProvider(): String = storage.getString(SHARED_USER_PROVIDER, "")!!

    fun getDateCreation(): String = storage.getString(SHARED_USER_DATE, "")!!

    fun getDescription(): String = storage.getString(SHARED_USER_DESCRIPTION, "")!!

    fun getLatOrigin():String = storagePublish.getString(SHARED_TRAVEL_LAT_ORIGIN, "")!!
    fun getLonOrigin():String = storagePublish.getString(SHARED_TRAVEL_LON_ORIGIN, "")!!
    fun getLatDest():String = storagePublish.getString(SHARED_TRAVEL_LAT_DEST, "")!!
    fun getLonDest():String = storagePublish.getString(SHARED_TRAVEL_LONG_DEST, "")!!
    fun getNameOrigin():String = storagePublish.getString(SHARED_TRAVEL_NAME_ORIGIN, "")!!
    fun getNameDest():String = storagePublish.getString(SHARED_TRAVEL_NAME_DEST, "")!!
    fun getDate():String = storagePublish.getString(SHARED_TRAVEL_DATE, "")!!
    fun getHourStart():String = storagePublish.getString(SHARED_TRAVEL_HOUR, "")!!
    fun getHourFinished():String = storagePublish.getString(SHARED_TRAVEL_HOUR_FINISH, "")!!
    fun getPassengers():String = storagePublish.getString(SHARED_TRAVEL_PASSENGERS, "")!!
    fun getVehicleModel():String = storagePublish.getString(SHARED_TRAVEL_VEHICLE_MODEL, "")!!
    fun getVehicleBrand():String = storagePublish.getString(SHARED_TRAVEL_VEHICLE_BRAND, "")!!
    fun getPrice():String = storagePublish.getString(SHARED_TRAVEL_PRICE, "")!!
    fun getPrefSmoke():String = storagePublish.getString(SHARED_TRAVEL_PREF_SMOKE, "")!!
    fun getPrefMusic():String = storagePublish.getString(SHARED_TRAVEL_PREF_MUSIC, "")!!
    fun getPrefEat():String = storagePublish.getString(SHARED_TRAVEL_PREF_EAT, "")!!
    fun getType():String = storagePublish.getString(SHARED_TYPE, "")!!
    fun getEdit():String = storagePublish.getString(SHARED_EDIT, "")!!
    fun getEditStatus():String = storagePublish.getString(SHARED_EDIT_STATUS, "")!!
    fun getJson():String = storagePublish.getString(SHARED_JSON_ROUTE, "")!!

    fun getNotiChat():String = storageNoti.getString(SHARED_NOTI_CHAT, "")!!
    fun getNotiReserve():String = storageNoti.getString(SHARED_NOTI_RESERVES, "")!!
    fun getNotiRequest():String = storageNoti.getString(SHARED_NOTI_REQUEST, "")!!
    fun getNotiReview():String = storageNoti.getString(SHARED_NOTI_REVIEWS, "")!!

}