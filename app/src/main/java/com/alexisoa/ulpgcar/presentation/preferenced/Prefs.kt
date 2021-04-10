package com.alexisoa.ulpgcar.presentation.preferenced

import android.content.Context

class Prefs(val context: Context){
    val SHARED_NAME =  "Mydtb"
    val SHARED_USER_EMAIL = "email"
    val SHARED_USER_PROVIDER = "provider"

    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveEmail(email: String){
        storage.edit().putString(SHARED_USER_EMAIL, email).apply()
    }

    fun saveProvider(provider: String){
        storage.edit().putString(SHARED_USER_PROVIDER, provider).apply()
    }

    fun wipe(){
        storage.edit().clear().apply()
    }

    fun getEmail(): String = storage.getString(SHARED_USER_EMAIL, "")!!

    fun getProvider(): String = storage.getString(SHARED_USER_PROVIDER, "")!!


}