package com.alexisoa.ulpgcar.presentation.ui.auth
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        goToLoginFragment()
        goToRegisterForm()
    }

    private fun goToLoginFragment(){
        binding.loginFragmentBut.setOnClickListener {
            //findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun goToRegisterForm(){
        binding.registerFormButton.setOnClickListener {
            //findNavController().navigate(R.id.action_registerFragment_to_registerFormFragment)
        }
    }
}