package com.alexisoa.ulpgcar.presentation.viewmodels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.loginprototype.domain.interactor.logininteractor.LoginInteractor

class LoginViewModelFactory(private val interactor: LoginInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(LoginInteractor::class.java).newInstance(interactor)
    }
}