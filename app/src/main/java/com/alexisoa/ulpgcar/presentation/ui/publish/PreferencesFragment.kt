package com.alexisoa.ulpgcar.presentation.ui.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentPreferencesBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs


class PreferencesFragment : Fragment(R.layout.fragment_preferences) {
    private lateinit var binding : FragmentPreferencesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreferencesBinding.bind(view)
        prefs.savePrefEat("No se permite comer")
        prefs.savePrefSmoke("No se permite fumar")
        prefs.savePrefMusic("No se permite poner música")
        listenerSwitchEat()
        listenerSwitchSmoke()
        listenerSwitchMusic()
        listenerBack()
        listenerNext()
    }

    private fun listenerSwitchMusic() {
        binding.switchMusic.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if (b){
                prefs.savePrefMusic("Se permite poner música")
            }else{
                prefs.savePrefMusic("No se permite poner música")
            }
        })
    }

    private fun listenerSwitchSmoke() {
        binding.switchSmoke.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if (b){
                prefs.savePrefSmoke("Se permite fumar")
            }else{
                prefs.savePrefSmoke("No se permite fumar")
            }
        })
    }

    private fun listenerSwitchEat(){
        binding.switchEat.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if (b){
                prefs.savePrefEat("Se permite comer")
            }else{
                prefs.savePrefEat("No se permite comer")
            }
        })
    }

    private fun listenerBack(){
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun listenerNext(){
        binding.next.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_preferences_to_priceFragment)
        }
    }


}