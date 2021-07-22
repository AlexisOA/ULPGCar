package com.alexisoa.ulpgcar.presentation.ui.publish

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentPassengerBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import kotlinx.android.synthetic.main.fragment_passenger.*

class PassengerFragment : Fragment(R.layout.fragment_passenger) {

    private lateinit var binding: FragmentPassengerBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPassengerBinding.bind(view)
        checkbtnNext()
        listenerOne()
        listenerTwo()
        listenerThree()
        listenerFour()
        listenerBack()
        listenerNext()

    }

    private fun checkbtnNext() {
        if (prefs.getType().equals("search")){
            binding.next.text = "Buscar"
            binding.questionPassenger.text = "¿Cuántas plazas quieres reservar?"
        }
    }

    private fun listenerOne() {
        binding.onePassenger.setOnClickListener{
            savePassengerNumber("1")
            onePassenger.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_ulpgc)
            onePassenger.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            disableButtonTwo()
            disableButtonThree()
            disableButtonFour()
            enableNextButton()
        }
    }

    private fun listenerTwo() {
        binding.twoPassengers.setOnClickListener{
            savePassengerNumber("2")
            twoPassengers.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_ulpgc)
            twoPassengers.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            disableButtonOne()
            disableButtonThree()
            disableButtonFour()
            enableNextButton()
        }

    }

    private fun listenerThree() {
        binding.threePassengers.setOnClickListener {
            savePassengerNumber("3")
            threePassengers.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_ulpgc)
            threePassengers.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            disableButtonOne()
            disableButtonTwo()
            disableButtonFour()
            enableNextButton()
        }
    }

    private fun listenerFour() {
        binding.fourPassengers.setOnClickListener {
            savePassengerNumber("4")
            fourPassengers.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_ulpgc)
            fourPassengers.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            disableButtonOne()
            disableButtonTwo()
            disableButtonThree()
            enableNextButton()
        }
    }

    private fun savePassengerNumber(number : String) {
        prefs.savePassengers(number)
    }

    private fun enableNextButton() {
        binding.next.setBackgroundResource(R.drawable.button_rounded)
        binding.next.isEnabled = true
    }

    private fun disableButtonOne(){
        binding.onePassenger.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.greaselight)
        onePassenger.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun disableButtonTwo(){
        binding.twoPassengers.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.greaselight)
        binding.twoPassengers.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun disableButtonThree(){
        binding.threePassengers.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.greaselight)
        binding.threePassengers.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun disableButtonFour(){
        binding.fourPassengers.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.greaselight)
        binding.fourPassengers.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun listenerBack() {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun listenerNext() {
        binding.next.setOnClickListener {
            if (prefs.getType().equals("publish")){
                findNavController().navigate(R.id.action_navigation_passenge_to_vehicleFeaturesFragment)
            }else{
                findNavController().navigate(R.id.action_navigation_passenge_to_navigation_searchList)
            }
        }

    }




}
