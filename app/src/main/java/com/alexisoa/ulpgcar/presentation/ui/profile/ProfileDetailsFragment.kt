package com.alexisoa.ulpgcar.presentation.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.review.ReviewRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentProfileDetailsBinding
import com.alexisoa.ulpgcar.domain.interactor.review.ReviewInteractorImp
import com.alexisoa.ulpgcar.presentation.ui.review.adapter.ReviewsAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.review.ReviewViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.review.ReviewViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList


class ProfileDetailsFragment : Fragment(R.layout.fragment_profile_details) {
    private lateinit var binding: FragmentProfileDetailsBinding
    private lateinit var user : User
    private lateinit var calendar : Calendar

    private val viewModelReview by viewModels<ReviewViewModel> { ReviewViewModelFactory(
            ReviewInteractorImp(ReviewRepositoryImp())
    ) }
    private lateinit var adapter: ReviewsAdapter
    private val reviewList = ArrayList<Review>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            user = it.getParcelable("userDetails")!!
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileDetailsBinding.bind(view)
        adapter = ReviewsAdapter(requireContext(), reviewList)
        calendar = Calendar.getInstance()
        setupDetails()
        setupRecyclerView()
        loadReviews()
        listenerBack()

    }

    private fun listenerBack() {
        binding.backProfile.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadReviews() {
        viewModelReview.getReviews(user.uid).observe(viewLifecycleOwner, Observer{ result : Resource<ArrayList<Review>> ->
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

    private fun setupRecyclerView() {
        with(binding){
            recyclerReviews.layoutManager = LinearLayoutManager(requireContext())
            recyclerReviews.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupDetails() {
        with(binding){
            fullName.text = user.fullName
            profileName.text = user.fullName
            calendar.timeInMillis = user.dateRegister
            binding.descriptionTxt.text = user.description
            Glide.with(requireContext())
                    .load(user.imageUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(profileImageProfileDetail)
        }
    }

}