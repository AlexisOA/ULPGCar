package com.alexisoa.ulpgcar.presentation.viewmodels.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.domain.interactor.review.ReviewInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class ReviewViewModel(val interactor : ReviewInteractor): ViewModel() {

    fun sendReview(review : Review) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.sendReviewInteractor(review))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun getReviews(uid: String) = liveData<Resource<ArrayList<Review>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.getReviewsInteractor(uid))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }
}