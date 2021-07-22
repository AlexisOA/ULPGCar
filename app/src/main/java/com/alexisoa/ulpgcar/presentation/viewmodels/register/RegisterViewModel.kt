package com.alexisoa.ulpgcar.presentation.viewmodels.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.domain.interactor.register.RegisterInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class RegisterViewModel(val interactor: RegisterInteractor) : ViewModel() {


    fun registerWithMailAndPassword(email: String, password: String, confirmPassword: String, fullName:String) = liveData<Resource<Boolean>>(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.signUpUser(email, password, confirmPassword, fullName))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}