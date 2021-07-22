package com.alexisoa.ulpgcar.presentation.ui.messages

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
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
import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.repository.messages.MessageRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentChatBinding
import com.alexisoa.ulpgcar.domain.interactor.messages.MessageInteractorImp
import com.alexisoa.ulpgcar.presentation.MainActivity
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.messages.adapter.ChatAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.messages.MessageViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.messages.MessagesViewModel
import com.alexisoa.ulpgcar.valueobject.ContainerNotifications
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.collection.LLRBNode


class ChatFragment : Fragment(R.layout.fragment_chat), ChatAdapter.OnUserListClickListener {
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatAdapter
    private val chatList = ArrayList<Chat>()
    private val viewModelMessage by viewModels<MessagesViewModel> { MessageViewModelFactory(
            MessageInteractorImp(MessageRepositoryImp())
    ) }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        checkNotification()
        observeListUsers()
        setupRecycler()
        adapter = ChatAdapter(requireContext(), chatList, this)
        addSnapshotToChatCollection()
    }

    private fun checkNotification() {
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_chat)
        if (prefs.getNotiChat().equals("yes")){
            if (badge !=  null ) {
                badge.isVisible =  false
                prefs.saveNotiChat("no")
                //badge.clearNumber()
            }else{
                var badge2 = bottomNavigation.getOrCreateBadge(R.id.navigation_chat)
                badge2.isVisible = true
                prefs.saveNotiChat("no")
            }
        }else{
            badge.isVisible = false
        }

    }

    private fun setupRecycler(){
        with(binding){
            recyclerMessagesList.setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.stackFromEnd = true
            recyclerMessagesList.layoutManager = linearLayoutManager
        }
    }

    private fun observeListUsers(){
        viewModelMessage.getUserList.observe(viewLifecycleOwner, Observer { result: Resource<ArrayList<Chat>> ->
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

    private fun onFailed(result: Resource.Failure<ArrayList<Chat>>) {
        binding.progressBar.visibility = View.GONE
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccess(result: Resource.Success<ArrayList<Chat>>) {
        binding.progressBar.visibility = View.GONE
        chatList.clear()
        chatList.addAll(result.data)
        //val tmp = chatList.distinct()

        //chatList.addAll(tmp)
        adapter = ChatAdapter(requireContext(), chatList, this)
        adapter.notifyDataSetChanged()

        binding.recyclerMessagesList.adapter = adapter
    }

    override fun onUserListClick(chat: Chat) {
        val arguments = Bundle().apply {
            putString("userUID" , chat.receiver)
            putString("nameUser", chat.fullName)
            putString("profileImage", chat.profileImage)
        }
        prefs.saveNotiChat("no")
        findNavController().navigate(R.id.action_navigation_messages_to_navigation_chat, arguments)
    }

    override fun OnUserLongClick(chat: Chat) {
        showDialogDelete(chat)
    }


    private fun showDialogDelete(chat: Chat){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Una vez eliminado un chat no podrá volver a recuperarlo. Por favor, confirme que quiere eliminarlo"
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            deleteChat(chat)
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

    private fun deleteChat(chat: Chat) {
        viewModelMessage.deleteChat(chat.receiver).observe(viewLifecycleOwner, Observer{ result: Resource<ArrayList<Chat>> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess(result)
                is Resource.Failure->onFailed(result)
            }
        })
    }


    private fun onNewChat(c : Chat) {
        if (chatList.contains(c)) return
        chatList.add(c)
        if (context != null) {
            //adapter = MessageAdapter(requireContext(), messagesList)
            adapter.notifyItemInserted(chatList.indexOf(c))
            binding.recyclerMessagesList.scrollToPosition(chatList.indexOf(c))


            //binding.recyclerChat.adapter = adapter
        }

    }

    private fun addSnapshotToChatCollection(){
        viewModelMessage.addChatSnapshot(::onNewChat).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            println("cargadoCHAT")

        })

    }


}