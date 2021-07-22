package com.alexisoa.ulpgcar.presentation.ui.travels

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
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.notifications.NotificationRepositoryImp
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.data.repository.reserves.ReservesRepositoryImp
import com.alexisoa.ulpgcar.data.singleton.DaysOfWeeks
import com.alexisoa.ulpgcar.data.singleton.MonthOfYear
import com.alexisoa.ulpgcar.databinding.FragmentMyReservesDetailsBinding
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.reserves.ReservesInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.ReservesAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationsViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.reserves.ReservesViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.reserves.ReservesViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_passenger.*
import kotlinx.android.synthetic.main.fragment_travels_details.*
import java.util.*

class MyReservesDetailsFragment : Fragment(R.layout.fragment_my_reserves_details) {
    private lateinit var binding : FragmentMyReservesDetailsBinding
    private lateinit var reserve: Reserve
    private lateinit var calendar: Calendar
    private lateinit var  calendarTime : Calendar

    private val viewModelReserve by viewModels<ReservesViewModel> { ReservesViewModelFactory(
            ReservesInteractorImp(ReservesRepositoryImp())
    ) }
    private val viewModelNotification by viewModels<NotificationViewModel> { NotificationsViewModelFactory(
            NotificationInteractorImp(NotificationRepositoryImp())
    ) }

    private val viewModelProfile by viewModels<ProfileViewModel> { ProfileViewModelFactory(
            ProfileInteractorImp(ProfileRepositoryImp())
    ) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            reserve = it.getParcelable("reserve")!!
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyReservesDetailsBinding.bind(view)
        calendar = Calendar.getInstance()
        calendarTime = Calendar.getInstance()
        listenerBack()
        setupDetails()
        listenerCancelReserve()
        listenerReview()
        listenerProfile()
    }

    private fun setupDetails() {
        with(binding){
            calendar.timeInMillis = reserve.dateTime
            val currentDay = DaysOfWeeks.dayWeek[calendar.get(Calendar.DAY_OF_WEEK)]
            val currentMonth = MonthOfYear.monthName[calendar.get(Calendar.MONTH)]
            val hourComplete = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                    Calendar.MINUTE))
            calendarTime.timeInMillis = reserve.timeFinish
            val hourFinished = String.format("%02d:%02d", calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(
                    Calendar.MINUTE))
            fullName.text = reserve.fullName
            cost_travel.text = "${reserve.cost}€"
            textPassengers.text = """${reserve.passengers} pasajeros"""
            nameBrand.text = reserve.brandCar
            colorCar.text = reserve.plateCar
            origin.text = cutAddress(reserve.nameOrigin)
            destination.text = cutAddress(reserve.nameDestination)
            dateTravel.text = "$currentDay ${calendar.get(Calendar.DAY_OF_MONTH)} de $currentMonth, $hourComplete"
            timeInit.text = hourComplete
            timeFinish.text = hourFinished
            statusReserv.text = reserve.statusReserve

            Glide.with(requireContext())
                    .load(reserve.profileImage)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.profileImageReserveDet)
        }
    }

    private fun listenerPopUp() {

        binding.popUpReserveDetails.setOnClickListener {
            val popupMenu : PopupMenu = PopupMenu(requireContext(), binding.popUpReserveDetails)
            popupMenu.menuInflater.inflate(R.menu.menu_review, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                item -> when(item.itemId){
                R.id.reviewItem -> goToReviewFragment()
                R.id.deleteReserveItem -> showDialogCancel("Tu reserva se eliminará definitavemente de tu historial.", "delete")
            }
                true
            })
            popupMenu.show()
        }
    }

    private fun goToReviewFragment() {
        val bundle = Bundle()
        bundle.putParcelable("reserve", reserve)
        findNavController().navigate(R.id.action_navigation_reserve_details_to_navigation_reviewFragment, bundle)
    }

    private fun listenerReview() {
        if (reserve.finished){
            binding.popUpReserveDetails.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_ulpgc))
            binding.cancelReserve.isEnabled = false
            binding.cancelReserve.setBackgroundResource(R.drawable.button_rounded_grease)
            listenerPopUp()
        }
    }


    private fun listenerCancelReserve() {
        binding.cancelReserve.setOnClickListener {
            showDialogCancel("Una vez cancelada la reserva no podrá volver a recuperarla. Por favor, confirme que quiere cancelarla", "cancel")
        }

    }

    private fun showDialogCancel(message : String, action:String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = message
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            if (action.equals("cancel")){
                cancelReserveVM()
            }else{
                deleteReserve()
            }

            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun deleteReserve() {
        viewModelReserve.deleteReserveFinished(reserve).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess(result, "delete")
                is Resource.Failure->onFailed(result)
            }

        })
    }

    private fun cancelReserveVM() {
        viewModelReserve.cancelTravel(reserve).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess(result, "cancel")
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

    private fun onSuccess( result : Resource.Success<Boolean>, state : String) {
        binding.progressBar.visibility = View.GONE
        //adapter.notifyDataSetChanged()
        if (state.equals("cancel")){
            sendNotificationToGroup(reserve, "ha cancelado su reserva. Vuelve a revisar las plazas diponibles del viaje con destino a ${cutAddress(reserve.nameDestination)}")
        }else{
            findNavController().popBackStack()
        }
    }

    private fun sendNotificationToGroup(reserve: Reserve, detail: String) {
        val title = "Solicitud"
        val detail = "${prefs.getName()} $detail"
        val myRequest = Volley.newRequestQueue(requireContext())
        viewModelNotification.sendNotification(title, detail, myRequest, reserve.uidOwnerReserve).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccessNotification()
                is Resource.Failure->onFailed(result)
            }
        })
    }

    private fun onSuccessNotification() {
        findNavController().popBackStack()
    }


    private fun listenerBack() {
        binding.backArrow.setOnClickListener { findNavController().popBackStack() }
    }

    private fun cutAddress(address : String) : String{
        if(address.length > 30) return address.substring(0,30) + "..."
        return address
    }

    private fun listenerProfile() {
        binding.goToProfile.setOnClickListener {
            viewModelProfile.getUserDetails(reserve.uidOwnerReserve).observe(viewLifecycleOwner, Observer{ result : Resource<User> ->
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
        findNavController().navigate(R.id.action_navigation_reserve_details_to_navigation_profileDetailsFragment, bundle)
    }


}
