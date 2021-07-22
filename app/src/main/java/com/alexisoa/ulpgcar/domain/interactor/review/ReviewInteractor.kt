package com.alexisoa.ulpgcar.domain.interactor.review

import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.valueobject.Resource

interface ReviewInteractor {
    suspend fun sendReviewInteractor(review:Review) : Resource<Boolean>
    suspend fun getReviewsInteractor(uid:String) : Resource<ArrayList<Review>>
}