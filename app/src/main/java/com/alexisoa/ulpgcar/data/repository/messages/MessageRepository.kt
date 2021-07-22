package com.alexisoa.ulpgcar.data.repository.messages

import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Message
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.valueobject.Resource

interface MessageRepository {
    suspend fun sendMessageRepo(message: Message, sender:String, receiver:String): Resource<Boolean>
    suspend fun readMessagesRepo(myId: String, userId: String) : Resource<ArrayList<Message>>
    suspend fun addMessageSnapshot(myId: String, userId: String, onNewMessage: (m: Message) -> Unit)  : Resource<Boolean>
    suspend fun addChatSnapshot(onNewChat: (m: Chat) -> Unit)  : Resource<Boolean>
    suspend fun getUsersFromRepo() : Resource<ArrayList<Chat>>
    suspend fun deleteChatRepo(uid:String): Resource<ArrayList<Chat>>
}