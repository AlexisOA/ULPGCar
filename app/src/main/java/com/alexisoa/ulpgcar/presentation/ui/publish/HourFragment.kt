package com.alexisoa.ulpgcar.presentation.ui.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentHourBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import java.util.*

class HourFragment : Fragment(R.layout.fragment_hour) {

    private lateinit var binding : FragmentHourBinding
    private lateinit var calendarStart: Calendar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHourBinding.bind(view)
        calendarStart = Calendar.getInstance()
        prefs.saveHourStart(calendarStart.timeInMillis.toString())
        binding.timePickerStart.setIs24HourView(true)
        observeTimeStart()
        listenerBack()
        listenerNext()

    }

    private fun observeTimeStart(){
        binding.timePickerStart.setOnTimeChangedListener(TimePicker.OnTimeChangedListener { view, hourOfDay, minute ->
            calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarStart.set(Calendar.MINUTE, minute)
            prefs.saveHourStart(calendarStart.timeInMillis.toString())
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
            println(prefs.getType())
            if (prefs.getType().equals("publish")){
                findNavController().navigate(R.id.action_navigation_hour_fragment_to_hourArrivedFragment)
            }else{
                findNavController().navigate(R.id.action_navigation_hour_fragment_to_passengerFragment)
            }

        }
    }




}