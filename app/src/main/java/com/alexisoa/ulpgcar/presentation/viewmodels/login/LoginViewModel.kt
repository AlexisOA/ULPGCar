package com.alexisoa.ulpgcar.presentation.viewmodels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.domain.interactor.login.LoginInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class LoginViewModel(val interactor: LoginInteractor): ViewModel() {

    fun loginWithMailAndPassword(email: String, password: String) = liveData<Resource<User>>(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.signInUser(email, password))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun loginWithGoogleAuth(authCredential: AuthCredential) = liveData<Resource<User>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.signInUserWithGoogle(authCredential))

        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }


    fun logoutUser() = liveData<Resource<Boolean>>(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.Logout())
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun resetPassword(email: String) = liveData<Resource<Boolean>>(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.resetPass(email))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}