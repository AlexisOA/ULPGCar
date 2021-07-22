package com.alexisoa.ulpgcar.presentation.ui.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepositoryImp
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentPasswordFormBinding
import com.alexisoa.ulpgcar.domain.interactor.login.LoginInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource


class PasswordFormFragment : Fragment(R.layout.fragment_password_form) {
    private lateinit var binding:  FragmentPasswordFormBinding

    private val viewModelReset by viewModels<LoginViewModel> { LoginViewModelFactory(LoginInteractorImp(AuthRepositoryImp())) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPasswordFormBinding.bind(view)
        listenerUpdatePassword()
        listenerBack()
    }

    private fun listenerBack() {
        binding.backToProfile.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun listenerUpdatePassword() {
        binding.savePassword.setOnClickListener {
            if (binding.currentEmail.text.isEmpty()){
                binding.currentEmail.setError("Este campo no puede estar vacío")
            }else if(checkEmail()){
                showDialogConfirm()
            }else{
                Toast.makeText(activity, "Escriba el e-mail con el que está autenticado en ULPGCar", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun checkEmail(): Boolean {
        return binding.currentEmail.text.toString().equals(prefs.getEmail())
    }


    private fun showDialogConfirm() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Se le va a enviar un e-mail para realizar el cambio de contraseña. Por favor, confirme que quiere continuar."
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            updatePasswordViewModel()
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun updatePasswordViewModel() {
        viewModelReset.resetPassword(binding.currentEmail.text.toString()).observe(viewLifecycleOwner, Observer{ result : Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess(result)
                is Resource.Failure->onFailed(result)
            }
        })
    }

    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailed(result: Resource.Failure<Boolean>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccess(result: Resource.Success<Boolean>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, "E-mail enviado correctamente, revise su bandeja de entrada.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }




}