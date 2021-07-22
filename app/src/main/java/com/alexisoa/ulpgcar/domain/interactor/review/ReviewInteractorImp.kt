package com.alexisoa.ulpgcar.domain.interactor.review

import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.data.repository.review.ReviewRepository
import com.alexisoa.ulpgcar.valueobject.Resource

class ReviewInteractorImp(private val repository: ReviewRepository):ReviewInteractor {
    override suspend fun sendReviewInteractor(review: Review): Resource<Boolean> {
        return repository.ReviewInteractorRepo(review)
    }

    override suspend fun getReviewsInteractor(uid: String): Resource<ArrayList<Review>> {
        return repository.getReviewsRepo(uid)
    }
}