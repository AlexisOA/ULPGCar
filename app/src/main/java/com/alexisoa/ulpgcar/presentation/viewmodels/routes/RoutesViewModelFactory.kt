package com.alexisoa.ulpgcar.presentation.viewmodels.routes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractor
import com.alexisoa.ulpgcar.domain.interactor.routes.RoutesInteractor
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractor

class RoutesViewModelFactory (private val routesInteractor : RoutesInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(RoutesInteractor::class.java).newInstance(routesInteractor)
    }
}
