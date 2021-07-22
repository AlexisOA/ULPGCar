package com.alexisoa.ulpgcar.presentation.ui.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.databinding.UsersMessageListBinding
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder
import com.bumptech.glide.Glide

class ChatAdapter(private val context : Context, private val chatList: ArrayList<Chat>,
                  private val itemClickListener: OnUserListClickListener): RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnUserListClickListener{
        fun onUserListClick(chat: Chat)
        fun OnUserLongClick(chat: Chat)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.users_message_list, parent, false))
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is MessageViewHolder -> holder.bind(chatList[position], position)
        }
    }

    inner class MessageViewHolder(itemView: View): BaseViewHolder<Chat>(itemView){
        val binding = UsersMessageListBinding.bind(itemView)
        override fun bind(item: Chat, position: Int) {
            binding.fullNameChat.text = item.fullName
            Glide.with(context)
                    .load(item.profileImage)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.profileImageChat)
            itemView.setOnClickListener { itemClickListener.onUserListClick(item) }
            itemView.setOnLongClickListener {
                itemClickListener.OnUserLongClick(item)
                true
            }
        }
    }
}