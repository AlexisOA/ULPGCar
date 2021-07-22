package com.alexisoa.ulpgcar.presentation.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.data.repository.request.RequestRepositoryImp
import com.alexisoa.ulpgcar.databinding.DialogUploadImageBinding
import com.alexisoa.ulpgcar.databinding.FragmentUploadImageBinding
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.request.RequestInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.request.adapter.RequestAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.request.RequestViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.request.RequestViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.bumptech.glide.Glide
import java.io.IOException

class UploadImageFragment : Fragment(R.layout.fragment_upload_image) {

    private lateinit var binding : FragmentUploadImageBinding
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST:Int = 2021

    private val viewModelProfile by viewModels<ProfileViewModel> { ProfileViewModelFactory(
            ProfileInteractorImp(ProfileRepositoryImp())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadImageBinding.bind(view)
        listenerOpenGallery()
        listenerBack()
        listenerUploadImage()
        binding.nameUpload.text = prefs.getName()
        Glide.with(requireContext())
                .load(prefs.getUrlImage())
                .fitCenter()
                .centerCrop()
                .into(binding.profilePic)
    }

    private fun listenerUploadImage() {
        binding.btnSaveImage.setOnClickListener {
            viewModelProfile.uploadImage(filePath!!).observe(viewLifecycleOwner, Observer{ result : Resource<Boolean> ->
                when(result){
                    is Resource.Loading->onLoading()
                    is Resource.Success->onSuccess(result)
                    is Resource.Failure->onFailed(result)
                }
            })
        }
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
        Toast.makeText(activity, "Imagen actualizada correctamente", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun listenerBack() {
        binding.btnCancelImage.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun listenerOpenGallery() {
        binding.uploadImage.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage(){
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null){
            println("ENTRO AL IF")
            if (data != null){
                filePath = data.data
                try {
                    println("ENTRÓ AL TRY")
                    var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
                    binding.profilePic.setImageBitmap(bitmap)
                    binding.btnSaveImage.isEnabled = true
                    binding.btnSaveImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_ulpgc))
                    binding.btnCancelImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.grease))
                    //println("OK")
                }catch (e: IOException){
                    e.printStackTrace()
                }
            }

        }
    }


}