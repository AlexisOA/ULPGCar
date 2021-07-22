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
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentDescriptionFormBinding
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource


class DescriptionFormFragment : Fragment(R.layout.fragment_description_form) {
    private lateinit var binding : FragmentDescriptionFormBinding
    private val viewModelProfile by viewModels<ProfileViewModel> { ProfileViewModelFactory(
            ProfileInteractorImp(ProfileRepositoryImp())
    ) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDescriptionFormBinding.bind(view)
        listenerUpdateDescription()
        listenerBack()
    }

    private fun listenerBack() {
        binding.backToProfile.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun listenerUpdateDescription() {
        binding.updateDescription.setOnClickListener {
            //updateDescriptionViewModel()
            println(binding.descriptionTextUpdate.text)
            showDialogConfirm()
        }
    }

    private fun showDialogConfirm() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Vas a actualizar tu descripción. Por favor, confirme que quiere hacerlo."
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            updateDescriptionViewModel()
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
    private fun updateDescriptionViewModel() {
        viewModelProfile.saveDescription(binding.descriptionTextUpdate.text.toString()).observe(viewLifecycleOwner, Observer{ result : Resource<Boolean> ->
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
        prefs.saveDescription(binding.descriptionTextUpdate.text.toString())
        findNavController().popBackStack()
    }


}