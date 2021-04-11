package com.alexisoa.ulpgcar.presentation.fragments.auth
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.databinding.FragmentInitBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.example.ulpgcarprototype.data.model.User

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
            findNavController().navigate(R.id.action_initFragment_to_registerFragment)
        }
    }

    private fun checkUserValue(){
        if(prefs.getEmail().isNotEmpty() && prefs.getProvider().isNotEmpty()){
            val user = User(prefs.getEmail(), prefs.getProvider())
            val action = InitFragmentDirections.actionInitFragmentToNavigationHome(user)
            findNavController().navigate(action)
            //findNavController().navigate(R.id.action_initFragment_to_navigation_home)
        }
    }
}