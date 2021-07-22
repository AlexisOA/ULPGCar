package com.alexisoa.ulpgcar.presentation.viewmodels.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Message
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.domain.interactor.messages.MessageInteractor
import com.alexisoa.ulpgcar.valueobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class MessagesViewModel(val interactor : MessageInteractor): ViewModel() {

    fun sendMessage(message: Message, sender: String, receiver:String) = liveData<Resource<Boolean>>(Dispatchers.IO){
        println("En el viewModel")
        emit(Resource.Loading())
        try {
            emit(interactor.sendMessage(message, sender, receiver))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun readMessages(myId: String, userId: String) = liveData<Resource<ArrayList<Message>>>(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.readMessages(myId, userId))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun deleteChat(userId: String) = liveData<Resource<ArrayList<Chat>>>(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(interactor.deleteChatInteractor(userId))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun addMessageSnapshot(myId: String, userId: String, onNewMessage: (m: Message) -> Unit) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.addMessageSnapshot(myId, userId, onNewMessage))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    fun addChatSnapshot(onNewChat: (c: Chat) -> Unit) = liveData<Resource<Boolean>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.addChatSnapshot(onNewChat))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }

    }

    val getUserList = liveData<Resource<ArrayList<Chat>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(interactor.getUsers())
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
}