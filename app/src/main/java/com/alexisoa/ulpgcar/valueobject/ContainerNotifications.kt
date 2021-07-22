package com.alexisoa.ulpgcar.valueobject

import com.alexisoa.ulpgcar.data.model.Chat

class ContainerNotifications (val chatsList : ArrayList<String> = ArrayList<String>()){

     fun existChatToList(uid: String) : Boolean{
        for (chat in chatsList) {
            if (chat.equals(uid)){
                return true
            }
        }
        return false
    }

     fun addUidChatToList(uid: String){
         for (chat in chatsList) {
             if (chat.equals(uid)){
                 return
             }
         }
         chatsList.add(uid)
    }
     fun getChatList(): ArrayList<String>{
        return chatsList
    }

}