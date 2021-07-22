package com.alexisoa.ulpgcar.data.repository.messages

import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Message
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await


class MessageRepositoryImp():MessageRepository{
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()

    override suspend fun sendMessageRepo(message: Message, sender:String, receiver:String): Resource<Boolean> {
        createChatIfDoesNotExist(sender, receiver)
        val  idChatSender = sender + receiver
        val  idChatReceiver = receiver + sender
        val messages = mapOf(
                "sender" to sender,
                "message" to message.message,
                "date" to message.date,
                "status" to message.status
        )
        fsDatabase.collection("users").document(sender).collection("chats").document(idChatSender).collection("messages").add(messages).await()
        fsDatabase.collection("users").document(receiver).collection("chats").document(idChatReceiver).collection("messages").add(messages).await()
        return Resource.Success(true)
    }

    private suspend fun createChatIfDoesNotExist(sender:String, receiver:String){
        val  idChatSender = sender + receiver
        val  idChatReceiver = receiver + sender
        val dataUse1 = mapOf(
                "receiver" to sender,
                "fullName" to prefs.getName(),
                "profileImage" to prefs.getUrlImage()
        )
        val refReceiver = fsDatabase.collection("users").document(receiver).get().await()
        val dataUse2 = mapOf(
                "receiver" to receiver,
                "fullName" to refReceiver.get("fullName"),
                "profileImage" to refReceiver.get("imageUrl")
        )
        val refChatUser1 = fsDatabase.collection("users").document(sender).collection("chats").document(idChatSender).get().await()
        val refChatUser2 = fsDatabase.collection("users").document(receiver).collection("chats").document(idChatReceiver).get().await()

        if(!refChatUser1.exists()){
            fsDatabase.collection("users").document(sender).collection("chats").document(idChatSender).set(dataUse2).await()
        }

        if(!refChatUser2.exists()){
            fsDatabase.collection("users").document(receiver).collection("chats").document(idChatReceiver).set(dataUse1).await()
        }
    }

    override suspend fun readMessagesRepo(sender: String, receiver: String): Resource<ArrayList<Message>> {
        val  idChat = sender + receiver
        val query2 = fsDatabase.collection("users").document(sender).collection("chats").document(idChat).collection("messages").orderBy("date", Query.Direction.DESCENDING).limit(15).get().await()
        var chatList = ArrayList<Message>()
        println(query2.documents.size)
        for (document in query2.documents) {
            val chat = document.toObject(Message::class.java)!!
            chatList.add(chat)
        }

        chatList.reverse()
        return Resource.Success(chatList)
    }

    override suspend fun getUsersFromRepo(): Resource<ArrayList<Chat>> {
        var chatList = ArrayList<Chat>()
        val query = fsDatabase.collection("users").document(fsAuth.currentUser.uid).collection("chats").get().await()
        for (document in query.documents){
            val chat = document.toObject(Chat::class.java)!!
            chatList.add(chat)
        }
        return Resource.Success(chatList)
    }

    override suspend fun deleteChatRepo(uid: String): Resource<ArrayList<Chat>> {
        val uidChat = "${prefs.getUID()}${uid}"
        val query = fsDatabase.collection("users").document(prefs.getUID()).collection("chats").document(uidChat).collection("messages").get().await()
        for (document in query.documents){
            fsDatabase.collection("users").document(prefs.getUID()).collection("chats").document(uidChat).collection("messages").document(document.id).delete().await()
        }
        fsDatabase.collection("users").document(prefs.getUID()).collection("chats").document(uidChat).delete().await()
        return getUsersFromRepo()
    }


    //activateObserver
    override suspend fun addMessageSnapshot(sender: String, receiver: String, onNewMessage: (m: Message) -> Unit)  : Resource<Boolean> {
        val  idChat = sender + receiver
        fsDatabase.collection("users").document(sender).collection("chats").document(idChat).collection("messages").addSnapshotListener(MetadataChanges.INCLUDE){ messages, error ->
            if (error == null){
                for (doc in messages!!.documentChanges){
                    val size = messages.documentChanges.size
                    if (size <= 2 && doc.type == DocumentChange.Type.ADDED){

                        println("Se añadió un mensaje")
                        val message = doc.document.toObject(Message::class.java)
                        onNewMessage(message)
                    }

                }
            }
        }

        return Resource.Success(true)
    }

    override suspend fun addChatSnapshot(onNewChat: (m: Chat) -> Unit)  : Resource<Boolean> {
        fsDatabase.collection("users").document(prefs.getUID()).collection("chats").addSnapshotListener(MetadataChanges.INCLUDE){ chats, error ->
            if (error == null){
                for (doc in chats!!.documentChanges){
                    val size = chats.documentChanges.size
                    if (size <= 2 && doc.type == DocumentChange.Type.ADDED){
                        println("Se añadió un chat")
                        val chat = doc.document.toObject(Chat::class.java)
                        onNewChat(chat)
                    }

                }
            }
        }

        return Resource.Success(true)
    }



}