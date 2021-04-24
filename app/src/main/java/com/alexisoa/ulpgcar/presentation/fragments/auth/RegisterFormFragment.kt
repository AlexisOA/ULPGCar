package com.alexisoa.ulpgcar.presentation.fragments.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentRegisterFormBinding
import com.alexisoa.ulpgcar.domain.interactor.register.RegisterInteractorImp
import com.alexisoa.ulpgcar.presentation.viewmodels.register.RegisterViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.register.RegisterViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.common.collect.Range


class RegisterFormFragment : Fragment(R.layout.fragment_register_form) {

    private lateinit var binding: FragmentRegisterFormBinding
    private val viewModelRegister by viewModels<RegisterViewModel> { RegisterViewModelFactory(RegisterInteractorImp(AuthRepositoryImp())) }
    private lateinit var awesomeValidation: AwesomeValidation


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterFormBinding.bind(view)
        addValidationToView()
        observeRegister()

    }

    private fun addValidationToView(){
        awesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        awesomeValidation.addValidation(activity, R.id.name, RegexTemplate.NOT_EMPTY, R.string.invalid_name)
        awesomeValidation.addValidation(activity, R.id.lastName, RegexTemplate.NOT_EMPTY, R.string.invalid_lastName)
        awesomeValidation.addValidation(activity, R.id.age, Range.closed(18,100), R.string.invalid_age)
        awesomeValidation.addValidation(activity, R.id.emailForm, Patterns.EMAIL_ADDRESS, R.string.invalid_email)
        awesomeValidation.addValidation(activity, R.id.passwordForm, ".{5,}", R.string.invalid_password)
        awesomeValidation.addValidation(activity, R.id.confirmPassword, ".{5,}", R.string.invalid_confirmPassword)
    }

    private fun observeRegister(){
        with(binding){
            registerEmail.setOnClickListener {
                if (awesomeValidation.validate()){
                    val fullName = name.text.toString() + " " + lastName.text.toString()
                    val radioSelectedID = radioGroupGenero.checkedRadioButtonId
                    val radioSelected = radioGroupGenero.findViewById<RadioButton>(radioSelectedID)
                    viewModelRegister.registerWithMailAndPassword(emailForm.text.toString(),
                            passwordForm.text.toString(), confirmPassword.text.toString(),
                            age.text.toString(), fullName, radioSelected.text.toString())
                            .observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
                                when(result){
                                    is Resource.Loading->onLoading()
                                    is Resource.Success->onSuccess()
                                    is Resource.Failure->onFailed(result)
                                }
                            })

                } else
                    Toast.makeText(activity, "Rellene todos los campos", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun FragmentRegisterFormBinding.onLoading() {
        progressBar.visibility = View.VISIBLE
        Toast.makeText(activity, "Cargando...", Toast.LENGTH_SHORT).show()
    }

    private fun FragmentRegisterFormBinding.onFailed(result: Resource.Failure<Boolean>) {
        progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun FragmentRegisterFormBinding.onSuccess() {
        progressBar.visibility = View.GONE
        Toast.makeText(activity, "Se ha enviado un e-mail de verificación a su cuenta de correo electrónico.", Toast.LENGTH_SHORT).show()
        val action = RegisterFormFragmentDirections.actionRegisterFormFragmentToLoginFragment()
        findNavController().navigate(action)
    }


}