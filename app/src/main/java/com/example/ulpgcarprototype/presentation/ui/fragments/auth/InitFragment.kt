package com.example.ulpgcarprototype.presentation.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ulpgcarprototype.R
import com.example.ulpgcarprototype.databinding.FragmentInitBinding

class InitFragment : Fragment(R.layout.fragment_init) {
    private lateinit var binding: FragmentInitBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInitBinding.bind(view)
        goToLoginFragment()
        goToRegisterFragment()
    }

    private fun goToLoginFragment(){
        binding.loginbutton.setOnClickListener {
            findNavController().navigate(R.id.action_initFragment_to_loginFragment)
        }
    }

    private fun goToRegisterFragment(){
        binding.registerbutton.setOnClickListener {
            findNavController().navigate(R.id.action_initFragment_to_registerFragment)
        }
    }
}