package com.alexisoa.ulpgcar.presentation.ui.travels

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentTravelsBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.TravelsTabAdapter
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout


class TravelsFragment : Fragment(R.layout.fragment_travels) {


    private lateinit var binding: FragmentTravelsBinding

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTravelsBinding.bind(view)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        setupTabLayout()
    }

    private fun setupTabLayout(){
        val adapter = TravelsTabAdapter(childFragmentManager)
        adapter.addFragment(MyReservesFragment(), "Mis reservas")
        adapter.addFragment(MyPublishFragment(), "Mis anuncios")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.blue_ulpgc), ContextCompat.getColor(requireContext(), R.color.blue_ulpgc))
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_car_black)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_article_24)
        val badgeReserve = tabLayout.getTabAt(0)!!.orCreateBadge
        val badgeRequest = tabLayout.getTabAt(1)!!.orCreateBadge
        setBadge(badgeReserve, badgeRequest)
    }

    private fun setBadge(badgeReserve: BadgeDrawable, badgeRequest: BadgeDrawable) {
        if (prefs.getNotiReserve().equals("yes")) {
            badgeReserve.backgroundColor = ContextCompat.getColor(requireContext(), R.color.red_publish)
            badgeReserve.isVisible = true
        } else {
            badgeReserve.isVisible = false
        }

        if (prefs.getNotiRequest().equals("yes")) {
            badgeRequest.backgroundColor = ContextCompat.getColor(requireContext(), R.color.red_publish)
            badgeRequest.isVisible = true
        } else {
            badgeRequest.isVisible = false
        }
    }
}