package com.alexisoa.ulpgcar.presentation.ui.search

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.repository.location.DataPlacesImp
import com.alexisoa.ulpgcar.data.repository.location.LocationRepoImp
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentSearchBinding
import com.alexisoa.ulpgcar.domain.interactor.search.SearchInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import java.util.*


class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding : FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        prefs.wipePublish()
        listenerGoToMap()
    }

    private fun listenerGoToMap() {
        binding.goSearch.setOnClickListener {
            prefs.saveType("search")
            findNavController().navigate(R.id.action_navigation_search_to_mapsFragment)
        }
    }

}