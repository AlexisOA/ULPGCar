package com.alexisoa.ulpgcar.presentation.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.databinding.FragmentInitBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs

class InitFragment : Fragment(R.layout.fragment_init) {
    private lateinit var binding: FragmentInitBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInitBinding.bind(view)
        goToLoginFragment()
        goToRegisterFragment()
        checkUserValue()

    }

    private fun goToLoginFragment(){
        binding.loginbutton.setOnClickListener {
            findNavController().navigate(R.id.action_initFragment_to_loginFragment)
        }
    }

    private fun goToRegisterFragment(){
        binding.registerbutton.setOnClickListener {
            findNavController().navigate(R.id.action_initFragment_to_registerFormFragment)
        }
    }

    private fun checkUserValue(){
        if(prefs.getUID().isNotEmpty() ){
            println(prefs.getTokenApp())
            val user = User(prefs.getName(), prefs.getEmail(),  prefs.getUID(),
                    prefs.getUrlImage(), prefs.getProvider(), prefs.getDateCreation().toLong(),
                    prefs.getTokenApp(), prefs.getDescription())
            val action = InitFragmentDirections.actionInitFragmentToNavigationSearch(user)
            findNavController().navigate(action)
        }
    }
}