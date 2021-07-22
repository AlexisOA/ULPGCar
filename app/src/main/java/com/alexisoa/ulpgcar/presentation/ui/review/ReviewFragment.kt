package com.alexisoa.ulpgcar.presentation.ui.review

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
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.data.repository.notifications.NotificationRepositoryImp
import com.alexisoa.ulpgcar.data.repository.review.ReviewRepositoryImp
import com.alexisoa.ulpgcar.data.singleton.DaysOfWeeks
import com.alexisoa.ulpgcar.data.singleton.MonthOfYear
import com.alexisoa.ulpgcar.databinding.FragmentReviewBinding
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.review.ReviewInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationsViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.review.ReviewViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.review.ReviewViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_travels_details.*
import java.util.*

class ReviewFragment : Fragment(R.layout.fragment_review) {
    private lateinit var binding: FragmentReviewBinding
    private lateinit var reserve: Reserve
    private lateinit var calendar: Calendar
    private lateinit var  calendarTime : Calendar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            reserve = it.getParcelable("reserve")!!
        }
    }

    private val viewModelReview by viewModels<ReviewViewModel> { ReviewViewModelFactory(
        ReviewInteractorImp(ReviewRepositoryImp())
    ) }

    private val viewModelNotification by viewModels<NotificationViewModel> { NotificationsViewModelFactory(
            NotificationInteractorImp(NotificationRepositoryImp())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReviewBinding.bind(view)
        calendar = Calendar.getInstance()
        calendarTime = Calendar.getInstance()
        setupDetails()
        listenerSendBtn()
    }

    private fun setupDetails(){
        with(binding){
            calendar.timeInMillis = reserve.dateTime
            val currentDay = DaysOfWeeks.dayWeek[calendar.get(Calendar.DAY_OF_WEEK)]
            val currentMonth = MonthOfYear.monthName[calendar.get(Calendar.MONTH)]
            val hourComplete = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            calendarTime.timeInMillis = reserve.timeFinish
            val hourFinished = String.format("%02d:%02d", calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE))
            fullName.text = reserve.fullName
            origin.text = cutAddress(reserve.nameOrigin)
            destination.text = cutAddress(reserve.nameDestination)
            dateTravel.text = "$currentDay ${calendar.get(Calendar.DAY_OF_MONTH)} de $currentMonth"
            timeInit.text = hourComplete
            timeFinish.text = hourFinished
            Glide.with(requireContext())
                .load(reserve.profileImage)
                .fitCenter()
                .centerCrop()
                .into(binding.profileImageTravelDetails)
        }
    }

    private fun cutAddress(address : String) : String{
        if(address.length > 25) return address.substring(0,25) + "..."
        return address
    }

    private fun listenerSendBtn() {
        binding.sendReview.setOnClickListener {
            println(binding.reviewTextBtn.text.toString())
            showDialogConfirm()
        }
    }

    private fun showDialogConfirm() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Vas a realizar una reseña a ${reserve.fullName}. Por favor, confirme que quiere enviarla."
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            sendReviewViewModel()
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun sendReviewViewModel() {
        val review = Review(binding.reviewTextBtn.text.toString(), Date().time, reserve.uidOwnerReserve, prefs.getUID(), prefs.getUrlImage(),
        prefs.getName(), reserve.nameOrigin, reserve.nameDestination)
        viewModelReview.sendReview(review).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess()
                is Resource.Failure->onFailed(result)
            }
        })
    }

    private fun onFailed(result: Resource.Failure<Boolean>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onSuccess() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, "Opinión enviada correctamente", Toast.LENGTH_SHORT).show()
        sendNotificationToReceiver()
        //findNavController().popBackStack()
    }

    private fun sendNotificationToReceiver() {
        val title = "Reseña"
        val detail = "Has recibido una reseña de ${prefs.getName()}. Accede a la sección opiniones de tu perfil para leerla."
        val myRequest = Volley.newRequestQueue(requireContext())
        viewModelNotification.sendNotification(title, detail, myRequest, reserve.uidOwnerReserve).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoadingNoti()
                is Resource.Success->onSuccessNotification()
                is Resource.Failure->onFailedNoti()
            }
        })

    }

    private fun onFailedNoti() {
    }

    private fun onLoadingNoti() {
    }

    private fun onSuccessNotification() {
        findNavController().popBackStack()
    }


}