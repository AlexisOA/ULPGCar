package com.alexisoa.ulpgcar.presentation.viewmodels.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.domain.interactor.registerinteractor.RegisterInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class RegisterViewModel(val interactor: RegisterInteractor) : ViewModel() {


    fun registerWithMailAndPassword(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.signUpUser(email, password))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}