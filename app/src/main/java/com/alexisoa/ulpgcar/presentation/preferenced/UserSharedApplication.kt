package com.alexisoa.ulpgcar.presentation.preferenced

import android.app.Application

class UserSharedApplication : Application(){
    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}