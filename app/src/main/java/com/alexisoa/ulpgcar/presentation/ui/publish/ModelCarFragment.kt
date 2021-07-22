package com.alexisoa.ulpgcar.presentation.ui.publish

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.data.repository.publish.PublishRepositoryImp
import com.alexisoa.ulpgcar.data.repository.vehicles.DataVehiclesImp
import com.alexisoa.ulpgcar.data.repository.vehicles.VehiclesRepoImp
import com.alexisoa.ulpgcar.databinding.FragmentModelCarBinding
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractorImp
import com.alexisoa.ulpgcar.presentation.ui.publish.adapters.ModelCarAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource

class ModelCarFragment : Fragment(R.layout.fragment_model_car), ModelCarAdapter.OnVehiclesClickListener{
    private lateinit var binding : FragmentModelCarBinding
    private lateinit var adapter: ModelCarAdapter
    private val viewModelVehicles by viewModels<PublishViewModel> { PublishViewModelFactory(VehiclesInteractorImp(VehiclesRepoImp(DataVehiclesImp())), PublishInteractorImp(PublishRepositoryImp())) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentModelCarBinding.bind(view)
        setupRecyclerView()
        setupSearchView()
        observeVehicles()
    }

    private fun setupRecyclerView(){
        with(binding){
            vehiclesList.layoutManager = LinearLayoutManager(requireContext())
            vehiclesList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupSearchView(){
        binding.vehiclesView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    adapter.filter.filter(newText)
                }
                return false
            }

        })
    }

    private fun observeVehicles(){
        viewModelVehicles.getDataVehicles()
            .observe(viewLifecycleOwner, Observer { result: Resource<List<Vehicle>> ->
                when(result){
                    is Resource.Loading->onLoading()
                    is Resource.Success-> onSuccess(result)
                    is Resource.Failure->onFailed(result)
                }
        })

    }

    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailed(result: Resource.Failure<List<Vehicle>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
        println(result.exception.message.toString())
    }
    private fun onSuccess(result: Resource.Success<List<Vehicle>>) {
        binding.progressBar.visibility = View.GONE
        adapter = ModelCarAdapter(requireContext(), result.data, this)
        binding.vehiclesList.adapter = adapter

    }

    override fun onVehicleClick(vehicle: Vehicle) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("brandCar", vehicle)
        findNavController().popBackStack()
    }


}