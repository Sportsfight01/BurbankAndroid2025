package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.data.model.UserFavoritesDisplayModel
import com.dmss.burbankapp.databinding.LayoutFavoriteDisplayItemBinding
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import timber.log.Timber

class FavoriteDisplayAdapter(var interaction: onItemClickListener) :
    ListAdapter<UserFavoritesDisplayModel, FavoriteDisplayAdapter.ItemViewHolder>(
        FavoritesComparator()
    ) {

    private var options: RequestOptions? = null

    init {
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

    }


    class ItemViewHolder(var binding: LayoutFavoriteDisplayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            model: UserFavoritesDisplayModel,
            options: RequestOptions?,
            interaction: onItemClickListener
        ) {
            if (model.FacadePermanentUrl != null) {
                var houseImageUrl: String? = model.FacadePermanentUrl
                if (model.FacadePermanentUrl.isNotEmpty()) {
                    if (model.FacadePermanentUrl.contains("~")) {
                        houseImageUrl = model.FacadePermanentUrl.replace("~", "")
                    }
                }
                houseImageUrl = "${AppConstants.PROFILEPIC_BASE}${houseImageUrl}".trim()
                GlideImageLoader(binding.ivHouse, binding.placeProgress).load(
                    houseImageUrl,
                    options
                )


            }
            binding.ivFavorite.setOnClickListener {
                interaction.unFavoriteItemClickListener(model, adapterPosition)
            }
            binding.rlPlace.setOnClickListener {
                interaction.itemClickListener(model)
            }
            if (model.displayEstateName.isNotEmpty()) {
                binding.tvHeader.text = ("${model.displayEstateName.trim()}, ${model.HouseName}")
                binding.tvStreet.text = ("${model.suburb},${model.street}")
            }



            Timber.e("design count:----> ${model.displayDesignCount!!.size}")
            model.displayDesignCount?.let {
                Timber.e("design count in:----> ${model.displayDesignCount!!.size}")
                binding.tvDesigns.text = ("${it.size} designs")
            } ?: run {
                Timber.e("design count out:----> ${model.displayDesignCount!!.size}")
                binding.tvDesigns.text = ("0 designs")
            }


        }

    }


    class FavoritesComparator : DiffUtil.ItemCallback<UserFavoritesDisplayModel>() {
        override fun areItemsTheSame(
            oldItem: UserFavoritesDisplayModel,
            newItem: UserFavoritesDisplayModel
        ) =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: UserFavoritesDisplayModel,
            newItem: UserFavoritesDisplayModel
        ) =
            oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var binding = LayoutFavoriteDisplayItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem, options, interaction)
        }
    }

    interface onItemClickListener {
        fun unFavoriteItemClickListener(model: UserFavoritesDisplayModel, position: Int)
        fun itemClickListener(model: UserFavoritesDisplayModel)
    }
}