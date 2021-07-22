package com.alexisoa.ulpgcar.presentation.ui.search.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.databinding.ResultsSearchBinding
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder

class DepartureAdapter(private val context: Context, private val locationList: ArrayList<Places>,
                       private val itemClickListener: OnLocationClickListener) :
        RecyclerView.Adapter<BaseViewHolder<*>>(){

    interface OnLocationClickListener{
        fun onLocationClick(location: Places)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return DepartureViewHolder(LayoutInflater.from(context).inflate(R.layout.results_search, parent, false))
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is DepartureViewHolder -> holder.bind(locationList[position], position)
        }
    }

    inner class DepartureViewHolder(itemView: View): BaseViewHolder<Places>(itemView){
        val binding = ResultsSearchBinding.bind(itemView)
        override fun bind(item: Places, position: Int) {
            binding.country.text = item.address.city
            binding.location.text = item.display_name
            itemView.setOnClickListener { itemClickListener.onLocationClick(item) }
        }

    }

}
