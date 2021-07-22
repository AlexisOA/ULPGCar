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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.notifications.NotificationRepositoryImp
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.data.singleton.DaysOfWeeks
import com.alexisoa.ulpgcar.data.singleton.MonthOfYear
import com.alexisoa.ulpgcar.databinding.FragmentMyPublishDetailsBinding
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.travels.TravelsInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationsViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_travels_details.*
import java.util.*

class MyPublishDetailsFragment : Fragment(R.layout.fragment_my_publish_details) {
    private lateinit var binding : FragmentMyPublishDetailsBinding
    private lateinit var travel: Travel
    private val viewModelTravels by viewModels<TravelsViewModel> { TravelsViewModelFactory(
        TravelsInteractorImp(TravelsRepositoryImp())
    ) }

    private val viewModelProfile by viewModels<ProfileViewModel> { ProfileViewModelFactory(
            ProfileInteractorImp(ProfileRepositoryImp())
    ) }
    private lateinit var calendar: Calendar
    private lateinit var  calendarTime : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            travel = it.getParcelable("travel")!!
        }
    }
    private val viewModelNotification by viewModels<NotificationViewModel> { NotificationsViewModelFactory(
            NotificationInteractorImp(NotificationRepositoryImp())
    ) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyPublishDetailsBinding.bind(view)
        calendar = Calendar.getInstance()
        calendarTime = Calendar.getInstance()
        prefs.wipePublish()
        setupDetails()
        listenerBack()
        listenerRequest()
        listenerPopUp()
        listenersetTravelFinished()
        listenerProfile()
    }

    private fun listenersetTravelFinished() {
        binding.travelFinished.setOnClickListener {
            showDialogFinishedTravel()
        }
    }

    private fun showDialogFinishedTravel() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Vas a proceder a establecer el viaje como finalizado. Todos los pasajeros podrán realizarte una reseña."
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            finishedTravel()
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }



    private fun finishedTravel() {
        viewModelTravels.setTravelFinished(travel).observe(viewLifecycleOwner, Observer{ result : Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoadingDelete()
                is Resource.Success->onSuccessFinished()
                is Resource.Failure->onFailedDelete(result)
            }
        })
    }

    private fun onSuccessFinished() {
        sendNotificationToGroup(travel, "Su viaje ha finalizado. ¡Valore su experiencia!")
        binding.progressBar.visibility = View.GONE
        findNavController().popBackStack()

    }

    private fun listenerPopUp() {
        binding.popUpPublishDetails.setOnClickListener {
            val popupMenu : PopupMenu = PopupMenu(requireContext(), binding.popUpPublishDetails)
            popupMenu.menuInflater.inflate(R.menu.popup_edit_delete, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                item -> when(item.itemId){
                    R.id.editItem -> listenerEdit()
                    R.id.deleteItem -> showDialogDelete()
                }
                true
            })
            popupMenu.show()
        }
    }

    private fun listenerRequest(){
        binding.goToNotifications.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("travel", travel)
            findNavController().navigate(R.id.action_navigation_mypublishdetails_to_requestFragment, bundle)
        }

    }



    private fun listenerBack() {
        binding.backArrow.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupDetails(){
        with(binding){
            if (travel.finished){
                travelFinished.isEnabled = false
                travelFinished.setBackgroundResource(R.drawable.button_rounded_grease)
                goToNotifications.isEnabled = false
            }
            calendar.timeInMillis = travel.dateTime
            val currentDay = DaysOfWeeks.dayWeek[calendar.get(Calendar.DAY_OF_WEEK)]
            val currentMonth = MonthOfYear.monthName[calendar.get(Calendar.MONTH)]
            val hourComplete = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            calendarTime.timeInMillis = travel.timeFinish
            val hourFinished = String.format("%02d:%02d", calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE))
            fullName.text = travel.fullname
            cost_travel.text = "${travel.cost}€"
            textPassengers.text = """${travel.passengers} pasajeros"""
            nameBrand.text = travel.modelVehicle
            colorCar.text = travel.plateVehicle
            origin.text = cutAddress(travel.nameOrigin)
            destination.text = cutAddress(travel.nameDestination)
            dateTravel.text = "$currentDay ${calendar.get(Calendar.DAY_OF_MONTH)} de $currentMonth, $hourComplete"
            timeInit.text = hourComplete
            timeFinish.text = hourFinished
            Glide.with(requireContext())
                    .load(travel.profileImage)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.profileImageMyPublish)
            println(travel.travelId)
        }
    }


    private fun cutAddress(address : String) : String{
        if(address.length > 30) return address.substring(0,30) + "..."
        return address
    }


    private fun listenerEdit(){
        if (!travel.finished){
            val bundle = Bundle()
            bundle.putParcelable("travel", travel)
            findNavController().navigate(R.id.action_myPublishDetailsFragment_to_navigation_edit_travels, bundle)
        }else{
            Toast.makeText(activity, "El viaje ya ha finalizado.", Toast.LENGTH_SHORT).show()
        }

    }


    private fun showDialogDelete(){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Una vez eliminado un anuncio no podrá volver a recuperarlo. Por favor, confirme que quiere eliminarlo"
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {

            deleteAnnouncement()
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

    private fun onFailed() {}

    private fun onLoading() {}

    private fun onSuccessNotification() {}

    private fun deleteAnnouncement(){
        viewModelTravels.deleteTravel(travel).observe(viewLifecycleOwner, Observer{ result : Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoadingDelete()
                is Resource.Success->onSuccessDelete(travel)
                is Resource.Failure->onFailedDelete(result)
            }
        })
    }

    private fun onLoadingDelete() {
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun onFailedDelete(result: Resource.Failure<Boolean>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccessDelete(travel: Travel) {
        binding.progressBar.visibility = View.GONE
        if (!travel.finished){
            sendNotificationToGroup(travel, "ha rechazado tu solicitud a una reserva.")
        }
        deleteAnnouncementAfterNotiGroup()

    }

    private fun deleteAnnouncementAfterNotiGroup() {
        viewModelTravels.deleteTravelAfterNotiGroup(travel).observe(viewLifecycleOwner, Observer{ result : Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoadingDelete()
                is Resource.Success->onSuccessAfterNotiGroup()
                is Resource.Failure->onFailedDelete(result)
            }
        })
    }
    private fun onSuccessAfterNotiGroup() {
        binding.progressBar.visibility = View.GONE
        findNavController().popBackStack()

    }

    private fun sendNotificationToGroup(travel: Travel, detail: String) {
        val title = "Reserva"
        val detail = "${prefs.getName()} $detail al viaje con destino ${cutAddress(travel.nameDestination)}"
        val myRequest = Volley.newRequestQueue(requireContext())
        viewModelNotification.sendNotificationGroup(title, detail, myRequest, travel.travelId).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccessNotification()
                is Resource.Failure->onFailed()
            }
        })
    }


    private fun listenerProfile() {
        binding.goToProfile.setOnClickListener {
            viewModelProfile.getUserDetails(travel.userId).observe(viewLifecycleOwner, Observer{ result : Resource<User> ->
                when(result){
                    is Resource.Loading->onLoadingProfile()
                    is Resource.Success->onSuccessProfile(result)
                    is Resource.Failure->onFailedProfile(result)
                }
            })
        }
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
        findNavController().navigate(R.id.action_navigation_mypublishdetails_to_navigation_profileDetailsFragment, bundle)
    }

}