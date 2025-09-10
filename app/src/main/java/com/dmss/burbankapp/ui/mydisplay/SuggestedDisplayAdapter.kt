package com.dmss.burbankapp.ui.mydisplay

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.SuggestedHomesDtoModel
import com.dmss.burbankapp.databinding.LayoutSuggestionDisplayHeaderBinding
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils

class SuggestedDisplayAdapter(var iSuggestedDisplay: ISuggestedDisplay,
                              var activity: Activity) :
    ListAdapter<SuggestedHomesDtoModel, SuggestedDisplayAdapter.ItemViewHolder>(SuggestedComparator()) {
    class ItemViewHolder(private val binding: LayoutSuggestionDisplayHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            model: SuggestedHomesDtoModel,
            options: RequestOptions?,
            iSuggestedDisplay: ISuggestedDisplay,
            activity: Activity
        ) {
            binding.tvHeader.text = ("${model.EstateName}, ${model.HouseName}")
            if (model.locations.isNotEmpty()) {
                binding.tvDesigns.visibility = View.VISIBLE
                if (model.locations.size > 1) {
                    binding.tvDesigns.text = ("${model.locations.size} DESIGNS")
                } else {
                    if (model.locations.isEmpty()) {
                        binding.tvDesigns.text = ("${model.locations.size} DESIGNS")
                    } else {
                        binding.tvDesigns.text = ("${model.locations.size} DESIGN")
                    }
                }
            } else {
                binding.tvDesigns.visibility = View.INVISIBLE
            }
            var houseImageUrl: String? = model.FacadePermanentUrl

            if (model.FacadePermanentUrl!=null && model.FacadePermanentUrl.isNotEmpty()) {
                if (model.FacadePermanentUrl.contains("~")) {
                    houseImageUrl = model.FacadePermanentUrl.replace("~", "")
                }
            }
            var isUserLoggedIn = CustomSharedPreferences(binding.ivFavorite.context).getUserLogin()

            houseImageUrl = "${AppConstants.PROFILEPIC_BASE}${houseImageUrl}".trim()
            GlideImageLoader(binding.house, binding.placeProgress).load(houseImageUrl, options)

            if (model.isFavorite &&  isUserLoggedIn) {

                binding.ivFavorite.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.ivFavorite.context,
                        R.drawable.fav_selected
                    )
                )
            } else {
                binding.ivFavorite.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.ivFavorite.context,
                        R.drawable.heart_bottom_svg
                    )
                )
            }

            binding.ivFavorite.setOnClickListener {
                if (isUserLoggedIn)
                {
                    if (model.isFavorite) {
                        binding.ivFavorite.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.ivFavorite.context,
                                R.drawable.heart_bottom_svg
                            )
                        )
                        model.isFavorite = false
                        iSuggestedDisplay.handleFavoriteAndUnFavorite(model, false)
                    } else {
                        binding.ivFavorite.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.ivFavorite.context,
                                R.drawable.fav_selected
                            )
                        )
                        model.isFavorite = true
                        iSuggestedDisplay.handleFavoriteAndUnFavorite(model, true)
                    }
                } else {
                    AppUtils.showPleaseLoginDialog(
                        binding.ivFavorite.context,
                        activity,
                        "Please login to add favourites"
                    )
                }
            }

        }
    }

    private var options: RequestOptions? = null

    init {
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

    }

    class SuggestedComparator : DiffUtil.ItemCallback<SuggestedHomesDtoModel>() {
        override fun areItemsTheSame(
            oldItem: SuggestedHomesDtoModel,
            newItem: SuggestedHomesDtoModel
        ) =
            oldItem.HouseName == newItem.HouseName

        override fun areContentsTheSame(
            oldItem: SuggestedHomesDtoModel,
            newItem: SuggestedHomesDtoModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val binding =
            LayoutSuggestionDisplayHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem, options, iSuggestedDisplay,activity)
        }
        holder.itemView.setOnClickListener {
            iSuggestedDisplay.selectSuggestedDisplay(currentItem)
        }
    }

    interface ISuggestedDisplay {
        fun selectSuggestedDisplay(model: SuggestedHomesDtoModel)
        fun handleFavoriteAndUnFavorite(model: SuggestedHomesDtoModel, isFavorite: Boolean)
    }


}