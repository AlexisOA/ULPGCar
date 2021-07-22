package com.alexisoa.ulpgcar.presentation.ui.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentHourArrivedBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import java.util.*


class HourArrivedFragment : Fragment(R.layout.fragment_hour_arrived) {
    private lateinit var binding : FragmentHourArrivedBinding
    private lateinit var calendarArrived: Calendar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHourArrivedBinding.bind(view)
        calendarArrived = Calendar.getInstance()
        prefs.saveHourFinished(calendarArrived.timeInMillis.toString())
        binding.timePickerDest.setIs24HourView(true)
        observeTimeDest()
        listenerBack()
        listenerNext()
    }

    private fun observeTimeDest(){
        binding.timePickerDest.setOnTimeChangedListener(TimePicker.OnTimeChangedListener { view, hourOfDay, minute ->
            calendarArrived.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarArrived.set(Calendar.MINUTE, minute)
            prefs.saveHourFinished(calendarArrived.timeInMillis.toString())
            binding.next.setBackgroundResource(R.drawable.button_rounded)
            binding.next.isEnabled = true
        })
    }

    private fun listenerBack(){
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun listenerNext(){
        binding.next.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_hour_arrived_to_navigation_passenge)
        }
    }



}