package com.alexisoa.ulpgcar.presentation.viewmodels.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractor

class NotificationsViewModelFactory(private val interactor: NotificationInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(NotificationInteractor::class.java).newInstance(interactor)
    }
}