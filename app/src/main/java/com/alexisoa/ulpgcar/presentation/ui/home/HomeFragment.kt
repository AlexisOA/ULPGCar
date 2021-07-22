package com.alexisoa.ulpgcar.presentation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentHomeBinding
import com.alexisoa.ulpgcar.databinding.FragmentResetPasswordBinding
import com.alexisoa.ulpgcar.domain.interactor.login.LoginInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<LoginViewModel> { LoginViewModelFactory(LoginInteractorImp(AuthRepositoryImp())) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        binding.email.text = prefs.getEmail()
        binding.proveedor.text = prefs.getName()
        observerLogout()
    }

    /*private fun observeHome(){
        BottomNavigationView.OnNavigationItemSelectedListener { item->
            when (item.itemId){
                R.id.navigation_home ->{
                    binding.email.text = prefs.getEmail()
                    binding.proveedor.text = prefs.getName()
                    true
                }
                else -> false
            }
        }

    }*/

    private fun observerLogout(){
        with(binding){
            logoutbutton.setOnClickListener {
                prefs.wipe()
                viewModel.logoutUser().observe(viewLifecycleOwner, Observer {result: Resource<Boolean> ->
                    when(result){
                        is Resource.Loading->onLoading()
                        is Resource.Success->onSuccess()
                        is Resource.Failure->onFailure(result)
                    }
                })
            }

        }
    }

    //TODO REFACTOR DUPLICATED CODE

    private fun FragmentHomeBinding.onFailure(result: Resource.Failure<Boolean>) {
        setProgressBarVisibility(View.GONE)
        setToastText( "Ocurrió un error:  ${result.exception.message}")
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun FragmentHomeBinding.onSuccess() {
        setProgressBarVisibility(View.GONE)
        //findNavController().navigate(R.id.action_navigation_home_to_initFragment)
    }

    private fun FragmentHomeBinding.onLoading() {
        setProgressBarVisibility(View.VISIBLE)
    }

    private fun FragmentHomeBinding.setProgressBarVisibility(visibility: Int) {
        progressBar.visibility = visibility
    }

    private fun setToastText(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}