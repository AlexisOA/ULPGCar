package com.alexisoa.ulpgcar.presentation.ui.travels.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.databinding.TravelsListBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_passenger.*
import kotlinx.android.synthetic.main.travels_list.view.*
import java.util.*

class ReservesAdapter(private val context : Context, private val reserveList: ArrayList<Reserve>,
                      private val itemClickListener: OnReserveClickListener
): RecyclerView.Adapter<BaseViewHolder<*>>() {


    interface OnReserveClickListener{
        fun onReserveDetailsClick(reserve: Reserve)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return ReserveViewHolder(LayoutInflater.from(context).inflate(R.layout.travels_list, parent, false))
    }

    override fun getItemCount(): Int {
        return reserveList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is ReserveViewHolder -> {
                holder.itemView.cv_travels.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)
                holder.bind(reserveList[position], position)
            }
        }
    }


    inner class ReserveViewHolder(itemView: View): BaseViewHolder<Reserve>(itemView){
        val binding = TravelsListBinding.bind(itemView)
        private val calendar = Calendar.getInstance()
        private val calendarTime = Calendar.getInstance()


        @SuppressLint("ResourceAsColor")
        override fun bind(item: Reserve, position: Int) {

            if (item.statusReserve.equals("Rechazada") || item.statusReserve.equals("Cancelada")){
                binding.status.setTextColor(ContextCompat.getColor(context, R.color.red_publish))
                binding.status.text = item.statusReserve
            }else if (item.statusReserve.equals("Aceptada")){
                binding.status.setTextColor(ContextCompat.getColor(context, R.color.gree_publish))
                binding.status.text = item.statusReserve
            }else if (item.finished){
                binding.status.setTextColor(ContextCompat.getColor(context, R.color.grease))
                binding.status.text = "Finalizado"
            }else {
                binding.status.text = "Pendiente"
            }
            calendar.timeInMillis = item.dateTime
            val hourComplete = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            calendarTime.timeInMillis = item.timeFinish
            val hourFinished = String.format("%02d:%02d", calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE))
            binding.fullNameText.text = cutText(item.fullName)
            binding.originText.text = cutText(item.nameOrigin)
            binding.destinationText.text = cutText(item.nameDestination)
            binding.priceText.text = "${item.cost}€"
            binding.timeInit.text = hourComplete
            binding.timeFinish.text = hourFinished

            Glide.with(context)
                    .load(item.profileImage)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.profileImageTravelList)
            itemView.setOnClickListener { itemClickListener.onReserveDetailsClick(item) }



        }


        private fun cutText(text: String) : String{
            if (text.length > 15) return text.substring(0,15) + "..."
            return text
        }


    }
}