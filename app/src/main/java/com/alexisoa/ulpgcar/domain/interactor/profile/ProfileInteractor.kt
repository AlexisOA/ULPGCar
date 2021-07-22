package com.alexisoa.ulpgcar.domain.interactor.profile

import android.net.Uri
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.valueobject.Resource

interface ProfileInteractor {
    suspend fun uploadImageInteractor(uri: Uri): Resource<Boolean>
    suspend fun getDataInteractor(uid:String) : Resource<User>
    suspend fun saveDescriptionInteractor(description:String): Resource<Boolean>
    suspend fun changePasswordInteractor(password: String) : Resource<Boolean>
}