package com.alexisoa.ulpgcar.presentation.ui.travels.adapter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.databinding.ResultsSearchBinding
import com.alexisoa.ulpgcar.databinding.TravelsListBinding
import com.alexisoa.ulpgcar.presentation.ui.publish.adapters.ModelCarAdapter
import com.alexisoa.ulpgcar.presentation.ui.search.adapters.DepartureAdapter
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.travels_list.view.*
import java.util.*
import kotlin.math.cos

class TravelsAdapter(private val context : Context, private val travelList: List<Travel>,
                     private val itemClickListener: OnTravelClickListener)
    : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnTravelClickListener{
        fun onTravelDetailsClick(travel: Travel)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return TravelViewHolder(LayoutInflater.from(context).inflate(R.layout.travels_list, parent, false))
    }
    override fun getItemCount(): Int {
        return travelList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is TravelViewHolder -> {
                holder.itemView.cv_travels.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)
                holder.bind(travelList[position], position)
            }
        }
    }

    inner class TravelViewHolder(itemView: View): BaseViewHolder<Travel>(itemView){
        val binding = TravelsListBinding.bind(itemView)
        private val calendar = Calendar.getInstance()
        private val calendarTime = Calendar.getInstance()
        override fun bind(item: Travel, position: Int) {
            if (!item.finished){
                binding.status.text = "Disponible"
            }else{
                binding.status.text = "Finalizado"
                binding.status.setTextColor(ContextCompat.getColor(context, R.color.grease))
            }
            calendar.timeInMillis = item.dateTime
            val hourComplete = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            calendarTime.timeInMillis = item.timeFinish
            val hourFinished = String.format("%02d:%02d", calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE))
            binding.fullNameText.text = cutText(item.fullname)
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
            itemView.setOnClickListener { itemClickListener.onTravelDetailsClick(item) }

        }

        private fun cutText(text: String) : String{
            if (text.length > 15) return text.substring(0,15) + "..."
            return text
        }

    }


}