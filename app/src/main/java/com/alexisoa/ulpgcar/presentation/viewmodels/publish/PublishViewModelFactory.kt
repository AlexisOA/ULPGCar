package com.alexisoa.ulpgcar.presentation.viewmodels.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractor
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractor

class PublishViewModelFactory(private val vehicleInteractor: VehiclesInteractor, private val publishInteractor: PublishInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(VehiclesInteractor::class.java, PublishInteractor::class.java).newInstance(vehicleInteractor, publishInteractor)
    }
}