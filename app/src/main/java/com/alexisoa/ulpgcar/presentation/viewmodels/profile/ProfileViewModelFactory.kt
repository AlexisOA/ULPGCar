package com.alexisoa.ulpgcar.presentation.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractor
import com.alexisoa.ulpgcar.domain.interactor.request.RequestInteractor

class ProfileViewModelFactory(private val interactor: ProfileInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ProfileInteractor::class.java).newInstance(interactor)
    }
}