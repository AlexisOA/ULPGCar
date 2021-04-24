package com.alexisoa.ulpgcar.presentation.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentLoginBinding
import com.alexisoa.ulpgcar.domain.interactor.login.LoginInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        const val GOOGLE_SIGN_IN = 200
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel> { LoginViewModelFactory(LoginInteractorImp(AuthRepositoryImp())) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        goToRegister()
        observeLoginWithEmail()
        observeLoginWithGoogle()
        observeResetPassword()
    }

    private fun observeResetPassword(){
        binding.changedPassButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }

    private fun goToRegister(){
        binding.registerbutton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observeLoginWithEmail(){
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
                            val actionUser = LoginFragmentDirections.actionLoginFragmentToNavigationHome(result.data)
                            prefs.saveEmail(result.data.email)
                            prefs.saveName(result.data.name)
                            findNavController().navigate(actionUser)
                        }
                        is Resource.Failure->{
                            progressBar.visibility = View.GONE
                            Toast.makeText(activity, "Ocurrió un error:  ${result.exception.message}", Toast.LENGTH_SHORT).show()
                            Log.e("ERROR", result.exception.message.toString())
                        }
                    }
                })
            }
        }

    }

    private fun observeLoginWithGoogle(){
        binding.loginGoogleBut.setOnClickListener {
            //Configuration
            val googleClient = GoogleSignIn.getClient(requireContext(), getGoogleSignInOptions())
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModelAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("ERROR", e.toString())
            }
        }
    }

    private fun viewModelAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        with(binding){
            viewModel.loginWithGoogleAuth(credential).observe(viewLifecycleOwner, Observer {result->
                when(result){
                    is Resource.Loading->{
                        progressBar.visibility = View.VISIBLE
                        Toast.makeText(activity, "Cargando...", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success->{
                        progressBar.visibility = View.GONE
                        val actionUser = LoginFragmentDirections.actionLoginFragmentToNavigationHome(result.data)
                        findNavController().navigate(actionUser)
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