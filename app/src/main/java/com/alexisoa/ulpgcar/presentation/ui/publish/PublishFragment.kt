package com.alexisoa.ulpgcar.presentation.ui.publish

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.data.repository.publish.PublishRepositoryImp
import com.alexisoa.ulpgcar.data.repository.vehicles.DataVehiclesImp
import com.alexisoa.ulpgcar.data.repository.vehicles.VehiclesRepoImp
import com.alexisoa.ulpgcar.databinding.FragmentPublishBinding
import com.alexisoa.ulpgcar.databinding.FragmentRegisterFormBinding
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.auth.RegisterFormFragmentDirections
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_publish.*
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import java.util.*


class PublishFragment : Fragment(R.layout.fragment_publish){
    private lateinit var binding : FragmentPublishBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPublishBinding.bind(view)
        prefs.wipePublish()
        listenerGotoPublish()
    }

    private fun listenerGotoPublish(){
        binding.goPublish.setOnClickListener {
            prefs.saveType("publish")
            findNavController().navigate(R.id.action_navigation_publisher_to_navigation_mapsFragment)
        }
    }




}