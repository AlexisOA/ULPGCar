package com.alexisoa.ulpgcar.data.repository.review

import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.valueobject.Resource

interface ReviewRepository {
    suspend fun ReviewInteractorRepo(review: Review): Resource<Boolean>
    suspend fun getReviewsRepo(uid:String) : Resource<ArrayList<Review>>
}