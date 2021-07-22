package com.alexisoa.ulpgcar.presentation.viewmodels.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexisoa.ulpgcar.domain.interactor.review.ReviewInteractor

class ReviewViewModelFactory(private val interactor: ReviewInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ReviewInteractor::class.java).newInstance(interactor)
    }
}