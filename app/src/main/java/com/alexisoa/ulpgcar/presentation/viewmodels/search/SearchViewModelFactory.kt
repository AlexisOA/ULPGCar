package com.alexisoa.ulpgcar.presentation.viewmodels.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.search.SearchInteractor

class SearchViewModelFactory(private val searchInteractor: SearchInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(SearchInteractor::class.java).newInstance(searchInteractor)
    }
}