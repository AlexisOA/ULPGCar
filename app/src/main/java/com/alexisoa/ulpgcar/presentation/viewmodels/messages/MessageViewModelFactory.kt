package com.alexisoa.ulpgcar.presentation.viewmodels.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.messages.MessageInteractor

class MessageViewModelFactory(private val interactor: MessageInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MessageInteractor::class.java).newInstance(interactor)
    }
}