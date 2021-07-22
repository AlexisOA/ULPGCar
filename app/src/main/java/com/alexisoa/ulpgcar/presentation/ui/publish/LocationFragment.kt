package com.alexisoa.ulpgcar.presentation.ui.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.model.Vehicle
import androidx.lifecycle.Observer
import com.alexisoa.ulpgcar.databinding.FragmentLocationBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import java.util.*


class LocationFragment : Fragment(R.layout.fragment_location) {
    private lateinit var binding: FragmentLocationBinding
    private lateinit var bundle: Bundle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLocationBinding.bind(view)
        bundle = Bundle()
        dataFindNavController()
        listenerBack()
        listenerFrom()
        listenerTo()
        listenerNext()
    }

    private fun dataFindNavController(){
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Places>("origin_publish")
            ?.observe(viewLifecycleOwner, Observer { origin->
                prefs.saveLatOrigin(origin.latitude)
                prefs.saveLonOrigin(origin.longitude)
                prefs.saveNameOrigin(origin.display_name)
                binding.originPublish.setText(cutText(origin.display_name))
            })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Places>("dest_publish")
            ?.observe(viewLifecycleOwner, Observer { destination->
                prefs.saveLatDest(destination.latitude)
                prefs.saveLonDest(destination.longitude)
                prefs.saveNameDest(destination.display_name)
                binding.destinationPublish.setText(cutText(destination.display_name))
                if(!binding.originPublish.text.toString().equals("")){
                    binding.next.setBackgroundResource(R.drawable.button_rounded)
                    binding.next.isEnabled = true
                }
            })
    }

    private fun listenerFrom(){
        binding.originPublish.setOnClickListener {
            bundle.putString("inputSearch", "fromPublish")
            //findNavController().navigate(R.id.jajaction_locationFragment_to_navigation_places, bundle)
        }
    }

    private fun listenerTo(){
        binding.destinationPublish.setOnClickListener {
            bundle.putString("inputSearch", "toPublish")
            //findNavController().navigate(R.id.action_locationFragment_to_navigation_places, bundle)
        }
    }

    private fun cutText(text: String):String{
        if(text.length > 28) return text.substring(0,28) + "..."
        return text
    }

    private fun listenerBack(){
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun listenerNext(){
        //binding.next.setOnClickListener { findNavController().navigate(R.id.action_navigation_locationfragment_to_publishDateFragment)}
    }

}