package com.alexisoa.ulpgcar.presentation.viewmodels.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class ProfileViewModel(val interactor : ProfileInteractor): ViewModel() {

    fun uploadImage(uri: Uri) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.uploadImageInteractor(uri))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun getUserDetails(uid: String) = liveData<Resource<User>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.getDataInteractor(uid))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun saveDescription(description: String) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.saveDescriptionInteractor(description))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun savePassword(password: String) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.changePasswordInteractor(password))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }
}