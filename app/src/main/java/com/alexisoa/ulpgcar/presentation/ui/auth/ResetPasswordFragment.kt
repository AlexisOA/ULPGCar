package com.alexisoa.ulpgcar.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentResetPasswordBinding
import com.alexisoa.ulpgcar.domain.interactor.login.LoginInteractorImp
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource


class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {
    private lateinit var binding: FragmentResetPasswordBinding
    private val viewModel by viewModels<LoginViewModel> { LoginViewModelFactory(LoginInteractorImp(AuthRepositoryImp()))}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentResetPasswordBinding.bind(view)
        observeSendEmailResetPassword()

    }

    private fun observeSendEmailResetPassword(){
        with(binding){
            resetPasswordButton.setOnClickListener {
                viewModel.resetPassword(emailReset.text.toString()).observe(viewLifecycleOwner, Observer {result: Resource<Boolean> ->
                    when(result){
                        is Resource.Loading->onLoading()
                        is Resource.Success->onSuccess()
                        is Resource.Failure->onFailure(result)
                    }
                })
            }
        }
    }

    private fun FragmentResetPasswordBinding.onFailure(result: Resource.Failure<Boolean>) {
        setProgressBarVisibility(View.GONE)
        setToastText( "Ocurrió un error:  ${result.exception.message}")
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun FragmentResetPasswordBinding.onSuccess() {
        setProgressBarVisibility(View.GONE)
        findNavController().navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment())
        setToastText("Se ha enviado un correo para restablecer su contraseña.")
    }

    private fun FragmentResetPasswordBinding.onLoading() {
        setProgressBarVisibility(View.VISIBLE)
    }

    private fun FragmentResetPasswordBinding.setProgressBarVisibility(visibility: Int) {
        progressBar.visibility = visibility
    }

    private fun setToastText(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}