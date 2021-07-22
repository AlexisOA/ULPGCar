package com.alexisoa.ulpgcar.presentation.ui.profile

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepositoryImp
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentProfileBinding
import com.alexisoa.ulpgcar.domain.interactor.login.LoginInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.login.LoginViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var calendar : Calendar
    private val viewModel by viewModels<LoginViewModel> { LoginViewModelFactory(LoginInteractorImp(AuthRepositoryImp())) }
    private val viewModelProfile by viewModels<ProfileViewModel> { ProfileViewModelFactory(
            ProfileInteractorImp(ProfileRepositoryImp())
    ) }
//action_navigation_profile_to_navigation_passwordFormFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        calendar = Calendar.getInstance()
        binding.personalinfo.visibility = View.VISIBLE
        binding.accountInfo.visibility = View.GONE
        setupDetails()
        observerLogout()
        listenerDetails()
        listenerAccount()
        listenerUploadImage()
        listenerPopUp()
        listenerEditDescription()
        listenerChangePassword()
        listenerMyReviewsReceived()
        checkBadge()
        listenerHelp()
        listenerUseConditions()
        listenerLegalWarning()
        listenerCovid()
    }

    private fun listenerHelp() {
        binding.helpProfile.setOnClickListener {
            goToBrowser("https://soportemaoa.wixsite.com/ulpgcar/faqs")

        }
    }

    private fun listenerUseConditions() {
        binding.useConditions.setOnClickListener {
            goToBrowser("https://soportemaoa.wixsite.com/ulpgcar/condiciones")

        }
    }

    private fun listenerLegalWarning() {
        binding.legalWarning.setOnClickListener {
            goToBrowser("https://soportemaoa.wixsite.com/ulpgcar/condiciones")

        }
    }

    private fun listenerCovid() {
        binding.covid.setOnClickListener {
            goToBrowser("https://soportemaoa.wixsite.com/ulpgcar/general-5")

        }
    }

    private fun goToBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun listenerMyReviewsReceived() {
        binding.myReviews.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_reviewsReceivedFragment)
        }
    }

    private fun listenerChangePassword() {
        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_passwordFormFragment)
        }
    }

    private fun listenerEditDescription() {
        binding.editDescription.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_descriptionFormFragment)
        }
    }

    private fun checkBadge() {
        if (prefs.getNotiReview().equals("yes")){
            prefs.saveNotiReviews("no")
            setBadge()
        }
    }

    private fun setBadge() {
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_profile)
        badge.isVisible = true
    }

    private fun cleanBadge(){
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_profile)
        badge.isVisible = false
    }

    private fun listenerPopUp() {
        binding.seeProfilePopUp.setOnClickListener {
            val popupMenu : PopupMenu = PopupMenu(requireContext(), binding.seeProfilePopUp)
            popupMenu.menuInflater.inflate(R.menu.menu_public_profile, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                item -> when(item.itemId){
                R.id.publicProfile -> goToFragmentProfile()
            }
                true
            })
            popupMenu.show()
        }
    }

    private fun goToFragmentProfile() {
        cleanBadge()
        getUserDetails()
    }

    private fun getUserDetails() {
        viewModelProfile.getUserDetails(prefs.getUID()).observe(viewLifecycleOwner, Observer{ result : Resource<User> ->
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

    private fun onFailed(result: Resource.Failure<User>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccess(result: Resource.Success<User>) {
        binding.progressBar.visibility = View.GONE
        val bundle = Bundle()
        bundle.putParcelable("userDetails", result.data)
        findNavController().navigate(R.id.action_navigation_profile_to_profileDetailsFragment, bundle)
    }

    private fun listenerUploadImage() {
        binding.editImage.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_profile_to_uploadImageFragment)
        }
    }



    private fun setupDetails() {
        with(binding){
            calendar.timeInMillis = prefs.getDateCreation().toLong()
            val currentMonth = calendar.get(Calendar.MONTH)+1
            val year = calendar.get(Calendar.YEAR)
            binding.dateRegister.text = """Fecha de registro: ${calendar.get(Calendar.DAY_OF_MONTH)}/$currentMonth/$year"""
            fullName.text = prefs.getName()
            profileName.text = prefs.getName()
            emailUser.text = prefs.getEmail()
            descriptionText.text = prefs.getDescription()
            Glide.with(requireContext())
                    .load(prefs.getUrlImage())
                    .fitCenter()
                    .centerCrop()
                    .into(imageProfileFragment)
        }
    }





    private fun listenerAccount() {
        binding.accountbtn.setOnClickListener {
            binding.accountInfo.visibility = View.VISIBLE
            binding.personalinfo.visibility = View.GONE
            binding.accountbtn.setTextColor(Color.parseColor("#0066a1"))
            binding.detailsbtn.setTextColor(Color.parseColor("#bfbfbf"))
        }
    }

    private fun listenerDetails() {
        binding.detailsbtn.setOnClickListener {
            binding.personalinfo.visibility = View.VISIBLE
            binding.accountInfo.visibility = View.GONE
            binding.accountbtn.setTextColor(Color.parseColor("#bfbfbf"))
            binding.detailsbtn.setTextColor(Color.parseColor("#0066a1"))
        }
    }

    private fun observerLogout(){
        with(binding){
            logoutbutton.setOnClickListener {
                prefs.wipe()
                prefs.wipePublish()
                viewModel.logoutUser().observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
                    when(result){
                        is Resource.Loading->onLoading()
                        is Resource.Success->onSuccess()
                        is Resource.Failure->onFailure(result)
                    }
                })
            }

        }
    }


    private fun FragmentProfileBinding.onFailure(result: Resource.Failure<Boolean>) {
        setProgressBarVisibility(View.GONE)
        setToastText( "Ocurrió un error:  ${result.exception.message}")
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun FragmentProfileBinding.onSuccess() {
        setProgressBarVisibility(View.GONE)
        findNavController().navigate(R.id.action_navigation_profile_to_initFragment)
    }

    private fun FragmentProfileBinding.onLoading() {
        setProgressBarVisibility(View.VISIBLE)
    }

    private fun FragmentProfileBinding.setProgressBarVisibility(visibility: Int) {
        progressBar.visibility = visibility
    }

    private fun setToastText(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }


}