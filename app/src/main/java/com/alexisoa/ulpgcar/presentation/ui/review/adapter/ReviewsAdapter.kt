package com.alexisoa.ulpgcar.presentation.ui.review.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Review
import com.alexisoa.ulpgcar.databinding.ReviewsListBinding
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.TravelsAdapter
import com.alexisoa.ulpgcar.valueobject.BaseViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.reviews_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class ReviewsAdapter(private val context : Context, private val reviewList: ArrayList<Review>): RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return ReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.reviews_list, parent, false))
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is ReviewViewHolder -> {
                holder.itemView.cv_reviews.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)
                holder.bind(reviewList[position], position)
            }
        }
    }

    inner class ReviewViewHolder(itemView: View): BaseViewHolder<Review>(itemView){
        val binding = ReviewsListBinding.bind(itemView)
        private val calendar = Calendar.getInstance()


        override fun bind(item: Review, position: Int) {
            calendar.timeInMillis = item.dateReview
            val currentMonth = calendar.get(Calendar.MONTH)+1
            val year = calendar.get(Calendar.YEAR)
            binding.dateReview.text = "${calendar.get(Calendar.DAY_OF_MONTH)}/$currentMonth/$year"
            binding.fullNameText.text = item.nameUser
            binding.travelReview.text = "Trayecto: ${cutText(item.nameTravelOrigin)} - ${cutText(item.nameTravelDestination)}"
            binding.reviewDescription.text = item.description
            Glide.with(context)
                    .load(item.imageUser)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.profileImageReview)

        }

        private fun cutText(text: String) : String{
            if (text.length > 15) return text.substring(0,15) + "..."
            return text
        }

    }
}