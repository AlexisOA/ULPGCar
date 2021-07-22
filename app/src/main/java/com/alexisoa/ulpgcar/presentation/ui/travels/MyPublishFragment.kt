package com.alexisoa.ulpgcar.presentation.ui.travels

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
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentMyPublishBinding
import com.alexisoa.ulpgcar.databinding.FragmentMyTravelsBinding
import com.alexisoa.ulpgcar.domain.interactor.travels.TravelsInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.TravelsAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView


class MyPublishFragment : Fragment(R.layout.fragment_my_publish), TravelsAdapter.OnTravelClickListener  {
    private lateinit var binding: FragmentMyPublishBinding
    private lateinit var adapter: TravelsAdapter
    private val viewModelTravels by viewModels<TravelsViewModel> { TravelsViewModelFactory(
        TravelsInteractorImp(TravelsRepositoryImp())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyPublishBinding.bind(view)
        checkBadge()
        setupRecycletView()
        loadTravels()
    }

    private fun checkBadge() {
        if (prefs.getNotiReserve().equals("yes")){
            prefs.saveNotiReserve("no")
            val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_travels)
            badge.isVisible = false
        }
    }

    private fun setupRecycletView(){
        with(binding){
            recyclerTravels.layoutManager = LinearLayoutManager(requireContext())
            recyclerTravels.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun loadTravels(){
        viewModelTravels.getAllTravels().observe(viewLifecycleOwner, Observer { result: Resource<List<Travel>> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess(result)
                is Resource.Failure->onFailed(result)
            }

        })
    }
    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailed(result: Resource.Failure<List<Travel>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccess( result : Resource.Success<List<Travel>>) {
        binding.progressBar.visibility = View.GONE
        //adapter.notifyDataSetChanged()
        adapter = TravelsAdapter(requireContext(), result.data, this)
        binding.recyclerTravels.adapter = adapter

    }

    override fun onTravelDetailsClick(travel: Travel) {
        val bundle = Bundle()
        bundle.putParcelable("travel", travel)
        findNavController().navigate(R.id.action_navigation_travels_to_myPublishDetailsFragment, bundle)
    }


}