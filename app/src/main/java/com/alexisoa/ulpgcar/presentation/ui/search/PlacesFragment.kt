package com.alexisoa.ulpgcar.presentation.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Address
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.repository.location.DataPlacesImp
import com.alexisoa.ulpgcar.data.repository.location.LocationRepoImp
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentPlacesBinding
import com.alexisoa.ulpgcar.domain.interactor.search.SearchInteractorImp
import com.alexisoa.ulpgcar.presentation.ui.search.adapters.DepartureAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class PlacesFragment : Fragment(R.layout.fragment_places), DepartureAdapter.OnLocationClickListener {


    private lateinit var binding: FragmentPlacesBinding
    private val viewModelSearch by viewModels<SearchViewModel> { SearchViewModelFactory(SearchInteractorImp(LocationRepoImp(DataPlacesImp()), TravelsRepositoryImp())) }
    private lateinit var searchCommand: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let{
            searchCommand = it.getString("inputSearch")!!
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlacesBinding.bind(view)

        setupRecyclerView()
        setupSearchView()
        observeSearchLocation()
        gpsClickListener()
    }

     fun gpsClickListener(){
        binding.gpsLocation.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                }else{
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
                }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100 && (grantResults.size > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getLastLocation()
        }else{
            Toast.makeText(activity, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(OnCompleteListener { task: Task<Location> ->
                val location = task.result
                if (location != null){
                    println("---------ARRIBA-------------" + location?.latitude  + location?.longitude+"------------ARRIBA---------------")
                    var placeGPS = Places("Mi ubicación", Address(""), location.latitude.toString(), location.longitude.toString())
                    onLocationClick(placeGPS)
                }else{
                    val locationRequest = LocationRequest.create().apply {
                        interval = 1
                        fastestInterval = 50
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        maxWaitTime = 100
                        numUpdates = 1
                    }

                    val locationCallBack = object : LocationCallback(){
                        override fun onLocationResult(p0: LocationResult) {
                            super.onLocationResult(p0)
                            val location1 = p0.lastLocation
                            println("----------ABAJO------------")
                            Log.d("TAG", "" + location1.latitude + location1.longitude);
                            println("----------ABAJO------------" + location1.latitude + location1.longitude +"-------------ABAJO--------------")
                            var placeGPS = Places("Mi ubicación", Address(""), location1.latitude.toString(), location1.longitude.toString())
                            onLocationClick(placeGPS)
                        }
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
                }
            })
        }else{
           startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
               .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun setupRecyclerView(){
        with(binding){
            searchList.layoutManager = LinearLayoutManager(requireContext())
            searchList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupSearchView(){
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrEmpty()){
                    viewModelSearch.setLocation(query!!)
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun observeSearchLocation(){
            viewModelSearch.searchLocation()
            .observe(viewLifecycleOwner, Observer{ result: Resource<ArrayList<Places>> ->
                when(result){
                    is Resource.Loading->onLoading()
                    is Resource.Success-> onSuccess(result)
                    is Resource.Failure->onFailed(result)
                }
            })
    }

    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
        Toast.makeText(activity, "Cargando...", Toast.LENGTH_SHORT).show()
    }

    private fun onFailed(result: Resource.Failure<ArrayList<Places>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
        println(result.exception.message.toString())
    }
    private fun onSuccess(result: Resource.Success<ArrayList<Places>>) {
        binding.progressBar.visibility = View.GONE
        binding.searchList.adapter = DepartureAdapter(requireContext(), result.data, this)
    }

    override fun onLocationClick(place: Places) {
        when(searchCommand){
            "from" ->findNavController().previousBackStackEntry?.savedStateHandle?.set("origin", place)
            "to"->findNavController().previousBackStackEntry?.savedStateHandle?.set("destination", place)
            "fromPublish"->findNavController().previousBackStackEntry?.savedStateHandle?.set("origin_publish", place)
            "toPublish" -> findNavController().previousBackStackEntry?.savedStateHandle?.set("dest_publish", place)
        }
        findNavController().popBackStack()
    }


}