package com.alexisoa.ulpgcar.presentation.ui.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.databinding.FragmentVehicleFeaturesBinding
import androidx.lifecycle.Observer
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import java.util.regex.Pattern


class VehicleFeaturesFragment : Fragment(R.layout.fragment_vehicle_features) {

    private lateinit var binding: FragmentVehicleFeaturesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVehicleFeaturesBinding.bind(view)
        listenerModelCar()
        dataFindNavController()
        listenerBack()
        listenerNext()
    }

    private fun listenerNext() {
        binding.next.setOnClickListener {
            if (validPlateCar(binding.plateCar.text.toString())){
                prefs.saveVehicleBrand(binding.plateCar.text.toString())
                findNavController().navigate(R.id.action_vehicleFeaturesFragment_to_navigation_preferences)
            }else{
                binding.plateCar.setError("Por favor, introduzca una matrícula válida")
            }
        }
    }

    private fun listenerBack() {
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun listenerModelCar(){
        with(binding){
            modelCar.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_features_vehicle_to_navigation_modelCar)
            }
        }
    }


    private fun dataFindNavController(){

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Vehicle>("brandCar")
            ?.observe(viewLifecycleOwner, Observer { brandCar->
                prefs.saveVehicleModel(brandCar.makeName)
                binding.modelCar.setText(cutText(brandCar.makeName))
            })
    }

    private fun cutText(text: String):String{
        if(text.length > 30) return text.substring(0,30) + "..."
        return text
    }

    private fun validPlateCar(plate: String): Boolean{
        return Pattern.compile("^[0-9]{1,4}(?!.*(LL|CH))[ABCDFGHJKLMNPRSTVWXYZ]{3}").matcher(plate).matches()
    }


}