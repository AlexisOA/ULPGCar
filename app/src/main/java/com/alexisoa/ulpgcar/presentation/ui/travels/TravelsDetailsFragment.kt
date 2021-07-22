package com.alexisoa.ulpgcar.presentation.ui.travels

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.notifications.NotificationRepositoryImp
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.data.repository.reserves.ReservesRepositoryImp
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.data.singleton.DaysOfWeeks
import com.alexisoa.ulpgcar.data.singleton.MonthOfYear
import com.alexisoa.ulpgcar.databinding.FragmentTravelsDetailsBinding
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.reserves.ReservesInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.travels.TravelsInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationsViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.reserves.ReservesViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.reserves.ReservesViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_edit_travel.*
import kotlinx.android.synthetic.main.fragment_passenger.*
import kotlinx.android.synthetic.main.fragment_travels_details.*
import java.util.*


class TravelsDetailsFragment : Fragment(R.layout.fragment_travels_details) {
    private lateinit var travel: Travel
    private lateinit var binding: FragmentTravelsDetailsBinding
    private val viewModelReserve by viewModels<ReservesViewModel> { ReservesViewModelFactory(
            ReservesInteractorImp(ReservesRepositoryImp())
    ) }
    private lateinit var calendar: Calendar
    private lateinit var  calendarTime : Calendar


    private val viewModelNotification by viewModels<NotificationViewModel> { NotificationsViewModelFactory(
            NotificationInteractorImp(NotificationRepositoryImp())
    ) }

    private val viewModelProfile by viewModels<ProfileViewModel> { ProfileViewModelFactory(
            ProfileInteractorImp(ProfileRepositoryImp())
    ) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            travel = it.getParcelable("travel")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTravelsDetailsBinding.bind(view)
        calendar = Calendar.getInstance()
        calendarTime = Calendar.getInstance()
        setupDetails()
        listenerReserve()
        listenerOpenChat()
        listenerBack()
        listenerPopUp()
    }

    private fun listenerPopUp() {
        binding.seeProfilePopUp.setOnClickListener {
            val popupMenu : PopupMenu = PopupMenu(requireContext(), binding.seeProfilePopUp)
            popupMenu.menuInflater.inflate(R.menu.menu_profile, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                item -> when(item.itemId){
                R.id.profileDetails -> getUserData()
            }
                true
            })
            popupMenu.show()
        }
    }

    private fun getUserData() {
        viewModelProfile.getUserDetails(travel.userId).observe(viewLifecycleOwner, Observer{ result : Resource<User> ->
            when(result){
                is Resource.Loading->onLoadingProfile()
                is Resource.Success->onSuccessProfile(result)
                is Resource.Failure->onFailedProfile(result)
            }
        })
    }

    private fun onLoadingProfile() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailedProfile(result: Resource.Failure<User>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccessProfile(result: Resource.Success<User>) {
        binding.progressBar.visibility = View.GONE
        val bundle = Bundle()
        bundle.putParcelable("userDetails", result.data)
        findNavController().navigate(R.id.action_travelsDetailsFragment_to_navigation_profileDetailsFragment, bundle)
    }

    private fun listenerBack() {
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }



    private fun listenerReserve() {
        binding.reserveBtn.setOnClickListener {
            showDialogReserve()
        }
    }

    private fun showDialogReserve() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Vas a reservar un viaje, El propietario del anuncio puede aceptar o rechazar tu solicitud."
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            reserveTravel()
            sendNotificationToReceiver(travel)
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun sendNotificationToReceiver(travel: Travel) {
        val title = "Solicitud"
        val detail = "${prefs.getName()} le ha enviado una solicitud de reserva a su viaje ${cutAddress(travel.nameOrigin)} -> ${cutAddress(travel.nameDestination)}"
        val myRequest = Volley.newRequestQueue(requireContext())
        viewModelNotification.sendNotification(title, detail, myRequest, travel.userId).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoadingNoti()
                is Resource.Success->onSuccessNotification()
                is Resource.Failure->onFailedNoti()
            }
        })
    }

    private fun onFailedNoti() {}

    private fun onLoadingNoti() {}

    private fun onSuccessNotification() {}

    private fun setupDetails(){
        with(binding){
            calendar.timeInMillis = travel.dateTime
            val currentDay = DaysOfWeeks.dayWeek[calendar.get(Calendar.DAY_OF_WEEK)]
            val currentMonth = MonthOfYear.monthName[calendar.get(Calendar.MONTH)]
            val hourComplete = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            calendarTime.timeInMillis = travel.timeFinish
            val hourFinished = String.format("%02d:%02d", calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE))
            fullName.text = travel.fullname
            cost_travel.text = travel.cost.toString() + " €"
            textPassengers.text = """${travel.passengers} pasajeros"""
            nameBrand.text = travel.modelVehicle
            colorCar.text = travel.plateVehicle
            origin.text = cutAddress(travel.nameOrigin)
            destination.text = cutAddress(travel.nameDestination)
            dateTravel.text = "$currentDay ${calendar.get(Calendar.DAY_OF_MONTH)} de $currentMonth"
            timeInit.text = hourComplete
            timeFinish.text = hourFinished
            smokePermission.text = travel.smokePermission
            musicPermission.text = travel.musicPermission
            eatPermission.text = travel.eatPermission
            Glide.with(requireContext())
                    .load(travel.profileImage)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.profileImageTravelDetails)
            println(travel.travelId)
            println(travel.userId)
        }
    }

    private fun cutAddress(address : String) : String{
        if(address.length > 25) return address.substring(0,25) + "..."
        return address
    }

    private fun listenerOpenChat(){
        binding.openChat.setOnClickListener {
            val bundle = Bundle()
            val arguments = Bundle().apply {
                putString("userUID" , travel.userId)
                putString("nameUser", travel.fullname)
                putString("profileImage", travel.profileImage)
            }
            bundle.putParcelable("travel", travel)
            findNavController().navigate(R.id.action_travelsDetailsFragment_to_chatFragment, arguments)
        }
    }

    private fun reserveTravel(){
        viewModelReserve.reserveTravel(travel).observe(viewLifecycleOwner, Observer{ result : Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess()
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

    private fun onSuccess() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show()
        binding.reserveBtn.setBackgroundResource(R.drawable.button_rounded_grease_light)
        binding.reserveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.reserveBtn.isEnabled = false
        findNavController().navigate(R.id.action_travelsDetailsFragment_to_navigation_travels)

    }

}