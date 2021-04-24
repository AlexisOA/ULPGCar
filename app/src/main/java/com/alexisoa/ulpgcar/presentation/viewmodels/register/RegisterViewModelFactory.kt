package com.alexisoa.ulpgcar.presentation.viewmodels.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.register.RegisterInteractor

class RegisterViewModelFactory(private val interactor: RegisterInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(RegisterInteractor::class.java).newInstance(interactor)
    }
}