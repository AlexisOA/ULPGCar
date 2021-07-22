package com.alexisoa.ulpgcar.presentation.ui.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentPublishDateBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import java.util.*


class PublishDateFragment : Fragment(R.layout.fragment_publish_date) {
    private lateinit var binding : FragmentPublishDateBinding
    private lateinit var calendar: Calendar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPublishDateBinding.bind(view)
        calendar = Calendar.getInstance()
        binding.calendarPublish.minDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, +3)
        binding.calendarPublish.maxDate = calendar.timeInMillis
        observeCalendar()
        listenerBack()
        listenerNext()
    }

    private fun observeCalendar(){
        binding.calendarPublish.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            prefs.saveDate(calendar.timeInMillis.toString())
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
            findNavController().navigate(R.id.action_navigation_publish_date_to_hourFragment)
        }
    }

}