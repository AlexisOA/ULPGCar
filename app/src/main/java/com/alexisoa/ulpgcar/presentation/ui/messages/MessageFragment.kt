package com.alexisoa.ulpgcar.presentation.ui.messages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Message
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.messages.MessageRepositoryImp
import com.alexisoa.ulpgcar.data.repository.notifications.NotificationRepositoryImp
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentMessageBinding
import com.alexisoa.ulpgcar.domain.interactor.messages.MessageInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.notifications.NotificationInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.profile.ProfileInteractorImp
import com.alexisoa.ulpgcar.presentation.MainActivity
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.messages.adapter.MessageAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.messages.MessageViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.messages.MessagesViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.notifications.NotificationsViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.profile.ProfileViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView


class MessageFragment : Fragment(R.layout.fragment_message) {
    private lateinit var userUid: String
    private lateinit var fullName: String
    private lateinit var profileImage: String
    private lateinit var binding : FragmentMessageBinding
    private lateinit var adapter: MessageAdapter
    private var messagesList = ArrayList<Message>()

    private val viewModelMessage by viewModels<MessagesViewModel> { MessageViewModelFactory(
            MessageInteractorImp(MessageRepositoryImp())
    ) }

    private val viewModelNotification by viewModels<NotificationViewModel> { NotificationsViewModelFactory(
        NotificationInteractorImp(NotificationRepositoryImp())
    ) }

    private val viewModelProfile by viewModels<ProfileViewModel> { ProfileViewModelFactory(
            ProfileInteractorImp(ProfileRepositoryImp())
    ) }


    private val channelName = "channelName"
    private val channelID= "channelID"
    private val notificationId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessageBinding.bind(view)
        requireArguments().let {
            userUid = it.getString("userUID")!!
            fullName = it.getString("nameUser")!!
            profileImage = it.getString("profileImage")!!
        }
        setupRecyclerView()
        adapter = MessageAdapter(requireContext(), messagesList)
        setupDetails()
        listenerSend()
        observeReadMessages(prefs.getUID(), userUid)
        addSnapshotToMessageCollection(prefs.getUID(), userUid)
        //createNotificationChannel()
        listenerSeeProfile()
    }

    private fun listenerSeeProfile() {
        binding.seeProfileMessage.setOnClickListener {
            viewModelProfile.getUserDetails(userUid).observe(viewLifecycleOwner, Observer{ result : Resource<User> ->
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
        findNavController().navigate(R.id.action_navigation_message_to_navigation_profileDetailsFragment, bundle)
    }

    private fun sendNotification(){
        val pendingIntent = NavDeepLinkBuilder(requireContext())
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.navigation_profile)
                .createPendingIntent()
        val notification = NotificationCompat.Builder(requireContext(), channelID).also {
            it.setContentTitle("Tienes un nuevo chat")
            it.setContentText("Acabas de recibir un nuevo mensaje")
            it.setSmallIcon(R.drawable.ic_message_24)
            it.setPriority(NotificationCompat.PRIORITY_HIGH)
            it.setContentIntent(pendingIntent)
            it.setAutoCancel(true)
        }.build()

        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_chat)
        badge.isVisible = true
        /*if (badge !=  null ) {
            badge.isVisible =  false
            badge.clearNumber()
        }*/

        with(NotificationManagerCompat.from(requireContext())){
            notify(notificationId, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = channelName
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelID, name, importance).apply {
                enableLights(true)
            }
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = requireContext().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun setupRecyclerView(){
        with(binding){
            recyclerChat.setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.stackFromEnd = true
            recyclerChat.layoutManager = linearLayoutManager
            //recyclerChat.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupDetails(){
        binding.fullnameChat.text = fullName
        Glide.with(requireContext())
                .load(profileImage)
                .fitCenter()
                .centerCrop()
                .into(binding.profileImageMessages)
    }

    private fun listenerSend(){
        binding.btnSend.setOnClickListener {
            if (!binding.message.text.toString().equals("")){
                sendMessage(prefs.getUID(), userUid, binding.message.text.toString())
            }else{
                Toast.makeText(activity, "Campo vacío", Toast.LENGTH_SHORT).show()
            }
            binding.message.setText("")
            //observeReadMessages(prefs.getUID(), userUid)
        }
    }

    private fun sendMessage(sender: String, receiver: String, message: String){
        println(message)
        val message = Message(prefs.getUID(),message)
        viewModelMessage.sendMessage(message, sender, receiver).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess()
                is Resource.Failure->onFailed(result)
            }

        })
    }

    private fun onLoading() {
        //binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailed(result: Resource.Failure<Boolean>) {
        //binding.progressBar.visibility = View.GONE

        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onFailedChat(result: Resource.Failure<ArrayList<Message>>) {
        Log.e("ERROR", result.exception.message.toString())
        Toast.makeText(activity, "${result.exception.message}", Toast.LENGTH_SHORT).show()
    }

    private fun onSuccess() {
        sendNotificationToReceiver()
    }

    private fun sendNotificationToReceiver() {
        val title = "Mensaje"
        val detail = "Tienes un nuevo mensaje de ${prefs.getName()}"
        val myRequest = Volley.newRequestQueue(requireContext())
        viewModelNotification.sendNotification(title, detail, myRequest, userUid).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccessNotification()
                is Resource.Failure->onFailed(result)
            }
        })
    }

    private fun onSuccessNotification() {
    }

    private fun onSuccessChat(result: Resource.Success<ArrayList<Message>>) {
        messagesList.addAll(result.data)
        val tmp = messagesList.distinct()
        messagesList.clear()
        messagesList.addAll(tmp)
        adapter = MessageAdapter(requireContext(), messagesList)
        adapter.notifyDataSetChanged()
        binding.recyclerChat.adapter = adapter
    }

    private fun observeReadMessages(myId: String, userId: String){
        viewModelMessage.readMessages(myId, userId).observe(viewLifecycleOwner, Observer { result: Resource<ArrayList<Message>> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccessChat(result)
                is Resource.Failure->onFailedChat(result)
            }

        })
    }

    private fun onNewMessage(m : Message) {
        messagesList.add(m)
        if (context != null) {
            //adapter = MessageAdapter(requireContext(), messagesList)
            adapter.notifyItemInserted(messagesList.indexOf(m))
            binding.recyclerChat.scrollToPosition(messagesList.indexOf(m))
            //sendNotification()
            //binding.recyclerChat.adapter = adapter
        }
    }

    private fun addSnapshotToMessageCollection(myId: String, userId: String){
        viewModelMessage.addMessageSnapshot(myId, userId, ::onNewMessage).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            println("cargado_MENSAJES")
        })

    }



}