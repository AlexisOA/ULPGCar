package com.alexisoa.ulpgcar.presentation.ui.publish.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.databinding.ResultModelCarBinding
import com.alexisoa.ulpgcar.databinding.ResultsSearchBinding
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder
import kotlinx.android.synthetic.main.result_model_car.view.*
import kotlinx.android.synthetic.main.travels_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class ModelCarAdapter(private val context: Context, private val vehiclesList: List<Vehicle>,
                      private val itemClickListener: OnVehiclesClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>(), Filterable {
    private var vehicleFilterList : List<Vehicle>
    init {
        vehicleFilterList = vehiclesList
    }
    interface OnVehiclesClickListener{
        fun onVehicleClick(vehicle: Vehicle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return ModelCarViewHolder(LayoutInflater.from(context).inflate(R.layout.result_model_car, parent, false))
    }

    override fun getItemCount(): Int {
        return vehicleFilterList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is ModelCarViewHolder -> {
                holder.itemView.cv_modelcar.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)
                holder.bind(vehicleFilterList[position], position)
            }
        }
    }

    inner class ModelCarViewHolder(itemView: View): BaseViewHolder<Vehicle>(itemView){
        val binding = ResultModelCarBinding.bind(itemView)
        override fun bind(item: Vehicle, position: Int) {
            binding.model.text = item.makeName
            itemView.setOnClickListener { itemClickListener.onVehicleClick(item) }
        }

    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    vehicleFilterList = vehiclesList
                } else {
                    val resultList : MutableList<Vehicle> = mutableListOf()
                    for (row in vehiclesList) {
                        if (row.makeName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    vehicleFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = vehicleFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                vehicleFilterList = results?.values as List<Vehicle>
                notifyDataSetChanged()
            }

        }
    }


}