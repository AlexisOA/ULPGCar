package com.alexisoa.ulpgcar.presentation.viewmodels.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.request.RequestInteractor
import com.alexisoa.ulpgcar.domain.interactor.reserves.ReservesInteractor

class RequestViewModelFactory(private val interactor: RequestInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(RequestInteractor::class.java).newInstance(interactor)
    }
}