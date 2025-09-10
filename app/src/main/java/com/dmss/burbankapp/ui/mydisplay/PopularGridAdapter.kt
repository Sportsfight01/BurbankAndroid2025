package com.dmss.burbankapp.ui.mydisplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.data.model.MostPopularHomesDTOModel
import com.dmss.burbankapp.databinding.LayoutPopularHomeDesignBinding
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants

class PopularGridAdapter(var listener: IPopularHomeDetailItemClick) :
    ListAdapter<MostPopularHomesDTOModel, PopularGridAdapter.ItemViewHolder>(PopularComparator()) {

    private var options: RequestOptions? = null

    init {
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

    }


    class ItemViewHolder(private val binding: LayoutPopularHomeDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mostPopularHomesDTOModel: MostPopularHomesDTOModel, options: RequestOptions?) {
            binding.tvCarter.text = mostPopularHomesDTOModel.houseName
            var houseImageUrl: String? = mostPopularHomesDTOModel.imageUrl

            if (mostPopularHomesDTOModel.imageUrl!=null && mostPopularHomesDTOModel.imageUrl.isNotEmpty()) {
                if (mostPopularHomesDTOModel.imageUrl.contains("~")) {
                    houseImageUrl = mostPopularHomesDTOModel.imageUrl.replace("~", "")
                }
            }

            if (mostPopularHomesDTOModel.locations.isNotEmpty()) {
                binding.tvLocations.visibility = View.VISIBLE
                if (mostPopularHomesDTOModel.locations.size > 1) {
                    binding.tvLocations.text =
                        ("${mostPopularHomesDTOModel.locations.size}  LOCATIONS")
                } else {
                    binding.tvLocations.text =
                        ("${mostPopularHomesDTOModel.locations.size}  LOCATION")
                }

            } else {
                binding.tvLocations.visibility = View.INVISIBLE
            }
            houseImageUrl = "${AppConstants.PROFILEPIC_BASE}${houseImageUrl}".trim()
            GlideImageLoader(binding.ivPopular, binding.placeProgress).load(houseImageUrl, options)

        }

    }


    class PopularComparator : DiffUtil.ItemCallback<MostPopularHomesDTOModel>() {
        override fun areItemsTheSame(
            oldItem: MostPopularHomesDTOModel,
            newItem: MostPopularHomesDTOModel
        ) =
            oldItem.Id == newItem.Id

        override fun areContentsTheSame(
            oldItem: MostPopularHomesDTOModel,
            newItem: MostPopularHomesDTOModel
        ) =
            oldItem.houseName == newItem.houseName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val binding =
            LayoutPopularHomeDesignBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem, options)
        }
        holder.itemView.setOnClickListener {
            listener.popularDetailItemClick(currentItem)
        }
        holder.itemView
    }


    interface IPopularHomeDetailItemClick {
        fun popularDetailItemClick(model: MostPopularHomesDTOModel)
    }


}