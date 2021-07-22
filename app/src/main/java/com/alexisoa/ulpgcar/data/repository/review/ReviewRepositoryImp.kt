package com.alexisoa.ulpgcar.data.repository.review

import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImp():ReviewRepository {
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()
    override suspend fun ReviewInteractorRepo(review: Review): Resource<Boolean> {
        fsDatabase.collection("users").document(review.uidOwner).collection("reviews").document().set(review).await()
        return Resource.Success(true)
    }

    override suspend fun getReviewsRepo(uid: String): Resource<ArrayList<Review>> {
        val query = fsDatabase.collection("users").document(uid).collection("reviews").get().await()
        var list = ArrayList<Review>()
        for (document in query.documents){
            list.add(document.toObject(Review::class.java)!!)
        }
        return Resource.Success(list)
    }
}