package com.alexisoa.ulpgcar.presentation.viewmodels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.loginprototype.domain.interactor.logininteractor.LoginInteractor
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class LoginViewModel(val interactor: LoginInteractor): ViewModel() {

    fun loginWithMailAndPassword(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.signInUser(email, password))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun logoutUser() = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.Logout())
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}