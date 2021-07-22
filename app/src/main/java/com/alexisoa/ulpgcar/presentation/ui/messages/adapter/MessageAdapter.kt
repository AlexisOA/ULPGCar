package com.alexisoa.ulpgcar.presentation.ui.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Message
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder
import kotlinx.android.synthetic.main.chat_item_right.view.*

class MessageAdapter (private val context : Context, private val chatList: ArrayList<Message>): RecyclerView.Adapter<BaseViewHolder<*>>(){
    //minuto 10:14 de la parte 8

    //https://www.youtube.com/watch?v=1mJv4XxWlu8&ab_channel=KODDev
    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1

    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        if(viewType == MSG_TYPE_RIGHT){
            return ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false))
        }else{
            return ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false))
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is ChatViewHolder -> holder.bind(chatList[position], position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList[position].sender == prefs.getUID()){
            return MSG_TYPE_RIGHT
        }else{
            return MSG_TYPE_LEFT
        }
    }

    inner class ChatViewHolder(itemView: View): BaseViewHolder<Message>(itemView){
        val textMessage = itemView.show_message.findViewById<TextView>(R.id.show_message)
        override fun bind(item: Message, position: Int) {
            textMessage.text = item.message
        }

    }


}