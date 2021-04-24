package com.alexisoa.ulpgcar.presentation.viewmodels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.login.LoginInteractor

class LoginViewModelFactory(private val interactor: LoginInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(LoginInteractor::class.java).newInstance(interactor)
    }
}