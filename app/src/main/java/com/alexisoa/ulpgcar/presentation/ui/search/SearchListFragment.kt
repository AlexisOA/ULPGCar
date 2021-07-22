package com.alexisoa.ulpgcar.presentation.ui.search

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
import com.alexisoa.ulpgcar.data.repository.location.DataPlacesImp
import com.alexisoa.ulpgcar.data.repository.location.LocationRepoImp
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentSearchListBinding
import com.alexisoa.ulpgcar.domain.interactor.search.SearchInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.TravelsAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*

class SearchListFragment : Fragment(R.layout.fragment_search_list), TravelsAdapter.OnTravelClickListener {

    private lateinit var binding : FragmentSearchListBinding
    private lateinit var adapter: TravelsAdapter
    private val viewModelSearch by viewModels<SearchViewModel> { SearchViewModelFactory(SearchInteractorImp(LocationRepoImp(DataPlacesImp()), TravelsRepositoryImp())) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchListBinding.bind(view)
        setupRecycletView()
        getTravels()
    }

    private fun setupRecycletView(){
        with(binding){
            recyclerSearch.layoutManager = LinearLayoutManager(requireContext())
            recyclerSearch.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun getTravels(){
        val gson = Gson()
        val json: String = prefs.getJson()
        val type: Type = object : TypeToken<ArrayList<com.alexisoa.ulpgcar.data.model.LatLng?>?>(){}.type
        val pointsList: ArrayList<com.alexisoa.ulpgcar.data.model.LatLng> = gson.fromJson(json, type)
        viewModelSearch.getTravels(prefs.getLatDest().toDouble(), prefs.getLonDest().toDouble(),
                prefs.getLatOrigin().toDouble(), prefs.getLonOrigin().toDouble(), pointsList,getDateTimeinMillis(), prefs.getPassengers() )
                .observe(viewLifecycleOwner, Observer{ result : Resource<List<Travel>> ->
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

    private fun onSuccess(result: Resource.Success<List<Travel>>) {
        binding.progressBar.visibility = View.GONE
        binding.originTextList.visibility = View.VISIBLE
        binding.destinationTextList.visibility = View.VISIBLE
        binding.originTextList.text = "Origen: ${cutText(prefs.getNameOrigin())}"
        binding.destinationTextList.text = "Destino: ${cutText(prefs.getNameDest())}"
        adapter = TravelsAdapter(requireContext(), result.data, this)
        binding.recyclerSearch.adapter = adapter
        //val bundle = Bundle()
        //bundle.putParcelableArrayList("travel", result.data as ArrayList<out Parcelable?>?)
        //findNavController().navigate(R.id.action_navigation_search_to_searchListFragment, bundle)

    }

    override fun onTravelDetailsClick(travel: Travel) {
        println("Has hecho click en: ${travel.nameDestination}")
        val bundle = Bundle()
        bundle.putParcelable("travel", travel)
        findNavController().navigate(R.id.action_searchListFragment_to_travelsDetailsFragment, bundle)
    }

    private fun getDateTimeinMillis(): Long{
        val calendarDateTime = Calendar.getInstance()
        val dateMillis = prefs.getDate().toLong()
        val timeMillis = prefs.getHourStart().toLong()
        val calendarDate = Calendar.getInstance()
        val calendarTime = Calendar.getInstance()
        calendarDate.timeInMillis = dateMillis
        calendarTime.timeInMillis = timeMillis

        calendarDateTime.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR))
        calendarDateTime.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH))
        calendarDateTime.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH))
        calendarDateTime.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
        calendarDateTime.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))
        calendarDateTime.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND))
        return calendarDateTime.timeInMillis
    }

    private fun cutText(address : String) : String{
        if(address.length > 75) return address.substring(0,75) + "..."
        return address
    }


}