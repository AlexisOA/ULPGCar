package com.alexisoa.ulpgcar.presentation.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.data.repository.review.ReviewRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentReviewsReceivedBinding
import com.alexisoa.ulpgcar.domain.interactor.review.ReviewInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.review.adapter.ReviewsAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.review.ReviewViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.review.ReviewViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource


class ReviewsReceivedFragment : Fragment(R.layout.fragment_reviews_received) {
    private lateinit var binding : FragmentReviewsReceivedBinding
    private val viewModelReview by viewModels<ReviewViewModel> { ReviewViewModelFactory(
            ReviewInteractorImp(ReviewRepositoryImp())
    ) }
    private lateinit var adapter: ReviewsAdapter
    private val reviewList = ArrayList<Review>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReviewsReceivedBinding.bind(view)
        adapter = ReviewsAdapter(requireContext(), reviewList)
        setupRecyclerView()
        listenrBack()
        loadReviews()
    }

    private fun setupRecyclerView() {
        with(binding){
            recyclerReviews.layoutManager = LinearLayoutManager(requireContext())
            recyclerReviews.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun loadReviews() {
        viewModelReview.getReviews(prefs.getUID()).observe(viewLifecycleOwner, Observer{ result : Resource<ArrayList<Review>> ->
            when(result){
                is Resource.Loading->onLoadingReview()
                is Resource.Success->onSuccessReview(result)
                is Resource.Failure->onFailedReview(result)
            }
        })
    }

    private fun onLoadingReview() {
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun onFailedReview(result: Resource.Failure<ArrayList<Review>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccessReview(result: Resource.Success<ArrayList<Review>>) {
        binding.progressBar.visibility = View.GONE
        adapter = ReviewsAdapter(requireContext(), result.data)
        binding.recyclerReviews.adapter = adapter
    }

    private fun listenrBack() {
        binding.backToProfile.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}