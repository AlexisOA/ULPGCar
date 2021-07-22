package com.alexisoa.ulpgcar.presentation.ui.request.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Request
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.databinding.RequestListBinding
import com.alexisoa.ulpgcar.databinding.TravelsListBinding
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.ReservesAdapter
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.request_list.view.*
import java.util.*

class RequestAdapter(private val context : Context, private val requestList:List<Request>,  private val itemClickListener: OnRequestClickListener ): RecyclerView.Adapter<BaseViewHolder<*>>()  {

    interface OnRequestClickListener{
        fun onRequestDetailsClick(request: Request)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return RequestViewHolder(LayoutInflater.from(context).inflate(R.layout.request_list, parent, false))
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is RequestViewHolder-> {
                holder.itemView.cardViewRequest.animation = AnimationUtils.loadAnimation(context, R.anim.slide)
                holder.bind(requestList[position], position)
            }
        }
    }

    inner class RequestViewHolder(itemView: View): BaseViewHolder<Request>(itemView){
        val binding = RequestListBinding.bind(itemView)
        private val calendar = Calendar.getInstance()
        override fun bind(item: Request, position: Int) {
            calendar.timeInMillis = item.dateReserve
            val currentMonth = calendar.get(Calendar.MONTH)+1
            val year = calendar.get(Calendar.YEAR)
            binding.fullNameReserve.text = item.fullName
            binding.statusRequest.text = item.statusReserve
            binding.dateReserve.setText("Solicitud enviada: ${calendar.get(Calendar.DAY_OF_MONTH)}/$currentMonth/$year")
            Glide.with(context)
                    .load(item.profileImage)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.profileImageRequestList)
            itemView.setOnClickListener{ itemClickListener.onRequestDetailsClick(item)}
        }


    }
}