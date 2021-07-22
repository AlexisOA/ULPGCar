package com.alexisoa.ulpgcar.domain.interactor.messages

import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Message
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.messages.MessageRepository
import com.alexisoa.ulpgcar.valueobject.Resource

class MessageInteractorImp(private val repository: MessageRepository):MessageInteractor {
    override suspend fun sendMessage(message: Message, sender:String, receiver:String): Resource<Boolean> {
        return repository.sendMessageRepo(message, sender, receiver)
    }

    override suspend fun readMessages(myId: String, userId: String): Resource<ArrayList<Message>> {
        return repository.readMessagesRepo(myId,userId)
    }

    override suspend fun addMessageSnapshot(myId: String, userId: String, onNewMessage: (m: Message) -> Unit)  : Resource<Boolean> {
        return repository.addMessageSnapshot(myId,userId,onNewMessage)
    }

    override suspend fun addChatSnapshot(onNewChat: (c: Chat) -> Unit): Resource<Boolean> {
        return repository.addChatSnapshot(onNewChat)
    }

    override suspend fun getUsers(): Resource<ArrayList<Chat>> {
        return repository.getUsersFromRepo()
    }

    override suspend fun deleteChatInteractor(uidUser: String): Resource<ArrayList<Chat>> {
        return repository.deleteChatRepo(uidUser)
    }
}