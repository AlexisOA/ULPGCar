package com.alexisoa.ulpgcar.presentation.viewmodels.travels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractor
import com.alexisoa.ulpgcar.domain.interactor.travels.TravelsInteractor
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractor

class TravelsViewModelFactory(private val interactor: TravelsInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(TravelsInteractor::class.java).newInstance(interactor)
    }
}