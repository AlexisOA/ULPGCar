package com.alexisoa.ulpgcar.data.repository.profile

import android.net.Uri
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.valueobject.Resource

interface ProfileRepository {
    suspend fun saveImageRepo(uri: Uri) : Resource<Boolean>
    suspend fun getDataRepo(uid:String) :Resource<User>
    suspend fun saveDescriptionRepo(description:String) : Resource<Boolean>
    suspend fun changePasswordRepo(password:String) : Resource<Boolean>
}