package com.alexisoa.ulpgcar.domain.interactor.messages

import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Message
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.valueobject.Resource

interface MessageInteractor {

    suspend fun sendMessage(message: Message, sender:String, receiver:String): Resource<Boolean>
    suspend fun readMessages(myId: String, userId: String) : Resource<ArrayList<Message>>
    suspend fun addMessageSnapshot(myId: String, userId: String, onNewMessage: (m: Message) -> Unit) : Resource<Boolean>
    suspend fun addChatSnapshot(onNewChat: (c: Chat) -> Unit) : Resource<Boolean>
    suspend fun getUsers(): Resource<ArrayList<Chat>>
    suspend fun deleteChatInteractor(uidUser:String): Resource<ArrayList<Chat>>
}