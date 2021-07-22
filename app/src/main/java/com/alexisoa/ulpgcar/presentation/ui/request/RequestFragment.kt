package com.alexisoa.ulpgcar.presentation.ui.request

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.notifications.NotificationRepositoryImp
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.data.repository.request.RequestRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentRequestBinding
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.request.RequestInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.request.adapter.RequestAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationsViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.request.RequestViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.request.RequestViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RequestFragment : Fragment(R.layout.fragment_request), RequestAdapter.OnRequestClickListener {

    private lateinit var binding : FragmentRequestBinding
    private val viewModelReserve by viewModels<RequestViewModel> { RequestViewModelFactory(
        RequestInteractorImp(RequestRepositoryImp())
    ) }
    private lateinit var adapter: RequestAdapter
    private lateinit var travel: Travel
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
        binding = FragmentRequestBinding.bind(view)
        listenerRequest()
        setupRecycletView()
    }

    private fun setupRecycletView() {
        with(binding){
            recyclerRequestList.layoutManager = LinearLayoutManager(requireContext())
            recyclerRequestList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun listenerRequest(){
            viewModelReserve.getAllRequests(travel.travelId).observe(viewLifecycleOwner, Observer{ result : Resource<List<Request>> ->
                when(result){
                    is Resource.Loading->onLoadingRequest()
                    is Resource.Success->onSuccessRequest(result)
                    is Resource.Failure->onFailedRequest(result)
                }
            })
        }
    private fun onLoadingRequest() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailedRequest(result: Resource.Failure<List<Request>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccessRequest(result: Resource.Success<List<Request>>) {
        binding.progressBar.visibility = View.GONE
        adapter = RequestAdapter(requireContext(), result.data, this)
        binding.recyclerRequestList.adapter = adapter

    }

    override fun onRequestDetailsClick(request: Request) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = request.dateReserve
        val currentMonth = calendar.get(Calendar.MONTH)+1
        val year = calendar.get(Calendar.YEAR)
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_request, null)
        val username = view.findViewById<TextView>(R.id.dialog_user_name)
        username.text = request.fullName
        val dateRequeste = view.findViewById<TextView>(R.id.dialog_date)
        dateRequeste.text = "Solicitud enviada: ${calendar.get(Calendar.DAY_OF_MONTH)}/$currentMonth/$year"
        val imageCircle = view.findViewById<CircleImageView>(R.id.profileImageDialogRequest)
        Glide.with(requireContext())
                .load(request.profileImage)
                .fitCenter()
                .centerCrop()
                .into(imageCircle)

        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val seeProfile = view.findViewById<TextView>(R.id.saw_profile)
        seeProfile.setOnClickListener {
            getDataUser(request)
            dialog.dismiss()
        }
        val buttonAccept = view.findViewById<Button>(R.id.btn_accept)
        buttonAccept.setOnClickListener {
            aceptRequest(request)
            sendNotificationToReceiver(request, "${request.fullName} ha aceptado tu solicitud a una reserva.")
            dialog.dismiss()
        }
        val buttonReject = view.findViewById<Button>(R.id.btn_reject)
        buttonReject.setOnClickListener {
            rejectRequest(request)
            sendNotificationToReceiver(request, "${request.fullName} ha rechazado tu solicitud a una reserva.")
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun getDataUser(request: Request) {
        viewModelProfile.getUserDetails(request.uidPassenger).observe(viewLifecycleOwner, Observer{ result : Resource<User> ->
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
        findNavController().navigate(R.id.action_navigation_request_fragment_to_navigation_profileDetailsFragment, bundle)
    }

    private fun rejectRequest(request: Request) {
        viewModelReserve.setRequestReject(request).observe(viewLifecycleOwner, Observer{ result : Resource<List<Request>> ->
            when(result){
                is Resource.Loading->onLoadingRequest()
                is Resource.Success->onSuccessRequest(result)
                is Resource.Failure->onFailedRequest(result)
            }
        })
    }

    private fun aceptRequest(request: Request) {
        viewModelReserve.setRequestAccept(request).observe(viewLifecycleOwner, Observer{ result : Resource<List<Request>> ->
            when(result){
                is Resource.Loading->onLoadingRequest()
                is Resource.Success->onSuccessRequest(result)
                is Resource.Failure->onFailedRequest(result)
            }
        })
    }


    private fun sendNotificationToReceiver(request: Request, detail: String) {
        val title = "Reserva"
        val details = "${prefs.getName()} $detail"
        val myRequest = Volley.newRequestQueue(requireContext())
        viewModelNotification.sendNotification(title, details, myRequest, request.uidPassenger).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccessNotification()
                is Resource.Failure->onFailed(result)
            }
        })

    }

    private fun onFailed(result: Resource.Failure<Boolean>) {
    }

    private fun onLoading() {
    }

    private fun onSuccessNotification() {
    }


}