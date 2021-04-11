package com.alexisoa.ulpgcar.presentation.fragments.auth
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentLoginBinding
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.loginprototype.domain.interactor.logininteractor.LoginInteractorImp
import com.example.ulpgcarprototype.data.model.User
import kotlinx.android.synthetic.main.activity_main.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel> { LoginViewModelFactory(LoginInteractorImp(AuthRepositoryImp())) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        goToRegister()
        observedata()
    }

    private fun goToRegister(){
        binding.registerbutton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observedata(){
        with(binding){
            binding.loginButton.setOnClickListener {
                viewModel.loginWithMailAndPassword(emailText.text.toString(), passwordText.text.toString()).observe(viewLifecycleOwner, Observer {result: Resource<User> ->
                    when(result){
                        is Resource.Loading->{
                            progressBar.visibility = View.VISIBLE
                            Toast.makeText(activity, "Cargando...", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Success->{
                            progressBar.visibility = View.GONE
                            //val action = LoginFragmentDirections.actionLoginFragmentToHomeGraph(result.data)
                            //findNavController().navigate(action)
                            val actionUser = LoginFragmentDirections.actionLoginFragmentToNavigationHome(result.data)
                            findNavController().navigate(actionUser)
                            //findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
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