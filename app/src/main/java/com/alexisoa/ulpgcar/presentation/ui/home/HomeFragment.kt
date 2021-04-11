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
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.loginprototype.domain.interactor.logininteractor.LoginInteractorImp
import com.example.ulpgcarprototype.data.model.User

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<LoginViewModel> { LoginViewModelFactory(LoginInteractorImp(AuthRepositoryImp())) }
    private lateinit var user: User
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        user = arguments?.let { HomeFragmentArgs.fromBundle(it).user }!!
        binding.email.text = user.name
        binding.proveedor.text = user.provider
        prefs.saveEmail(user.name)
        prefs.saveProvider(user.provider)
        observerLogout()

    }

    private fun observerLogout(){
        with(binding){
            logoutbutton.setOnClickListener {
                prefs.wipe()
                viewModel.logoutUser().observe(viewLifecycleOwner, Observer {result: Resource<Boolean> ->
                    when(result){
                        is Resource.Loading->{
                            progressBar.visibility = View.VISIBLE
                            Toast.makeText(activity, "Cargando...", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Success->{
                            progressBar.visibility = View.GONE
                            //val action = LoginFragmentDirections.actionLoginFragmentToHomeGraph(result.data)
                            //findNavController().navigate(action
                            //findNavController().popBackStack(R.id.navigation_home, true)
                            findNavController().navigate(R.id.action_navigation_home_to_initFragment)
                        }
                        is Resource.Failure->{
                            progressBar.visibility = View.GONE
                            Toast.makeText(activity, "Ocurrió un error ${result.exception.message}", Toast.LENGTH_SHORT).show()
                            Log.e("ERROR", result.exception.message.toString())
                        }
                    }
                })
            }

        }
    }
}