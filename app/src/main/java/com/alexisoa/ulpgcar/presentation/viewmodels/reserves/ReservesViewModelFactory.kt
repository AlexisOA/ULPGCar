package com.alexisoa.ulpgcar.presentation.viewmodels.reserves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.reserves.ReservesInteractor
import com.alexisoa.ulpgcar.domain.interactor.travels.TravelsInteractor

class ReservesViewModelFactory(private val interactor: ReservesInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ReservesInteractor::class.java).newInstance(interactor)
    }
}