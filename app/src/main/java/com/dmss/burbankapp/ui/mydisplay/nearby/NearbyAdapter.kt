package com.dmss.burbankapp.ui.mydisplay.nearby

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.HouseDetailsByHouseType
import com.dmss.burbankapp.databinding.LayoutNearbyListItemBinding
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import timber.log.Timber

class NearbyAdapter(var iOnItemClickListener: IOnItemClickListener,
                    var activity: Activity
) :
    ListAdapter<HouseDetailsByHouseType, NearbyAdapter.ItemViewHolder>(NearByComparator()) {

    private var options: RequestOptions? = null

    init {
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

    }

    class ItemViewHolder(var binding: LayoutNearbyListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            model: HouseDetailsByHouseType,
            options: RequestOptions?,
            OnItemClickListener: IOnItemClickListener,
            activity: Activity
        ) {
            binding.tvFirst.text = ("${model.HouseName} ${model.HouseSize}")

            binding.tvSecond.text = model.displayEstateName
            binding.tvThird.text = model.suburb
            binding.tvFourth.text = "${model.Facade} Facade"

            binding.tvcar.text = model.CarSpace.toString()
            binding.tvBath.text = model.BathRooms.toString()
            binding.tvBed.text = model.BedRooms.toString()
            var isUserLoggedIn = CustomSharedPreferences(binding.ivFavorite.context).getUserLogin()

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

            Timber.e("Favorite API mainResponse ${model.isFavorite}")

            binding.ivFavorite.setOnClickListener {

                var isUserLoggedIn = CustomSharedPreferences(binding.ivFavorite.context).getUserLogin()
                Timber.e("Favorite API ${model.isFavorite}")
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
                        Timber.e("Favorite API false")
                        OnItemClickListener.handleFavoriteAndUnFavorite(model, false)
                    } else {
                        binding.ivFavorite.setImageDrawable(
                            ContextCompat.getDrawable(
                                binding.ivFavorite.context,
                                R.drawable.fav_selected
                            )
                        )
                        model.isFavorite = true
                        Timber.e("Favorite API true")
                        OnItemClickListener.handleFavoriteAndUnFavorite(model, true)
                    }
                } else {
                    AppUtils.showPleaseLoginDialog(
                        binding.ivFavorite.context,
                        activity,
                        "Please login to add favourites"
                    )
                }
            }

            var houseImageUrl: String? = null
            model.FacadePermanentUrl?.let {facadeImage->
                if (facadeImage.isNotEmpty()) {
                    houseImageUrl = facadeImage
                    if (facadeImage.contains("~")) {
                        houseImageUrl = facadeImage.replace("~", "")
                    }
                }
                houseImageUrl?.let {
                    houseImageUrl = "${AppConstants.PROFILEPIC_BASE}${it}".trim()
                    GlideImageLoader(binding.ivPlace, binding.placeProgress).load(houseImageUrl, options)
                }

            }



        }

    }

    class NearByComparator : DiffUtil.ItemCallback<HouseDetailsByHouseType>() {
        override fun areItemsTheSame(
            oldItem: HouseDetailsByHouseType,
            newItem: HouseDetailsByHouseType
        ) =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: HouseDetailsByHouseType,
            newItem: HouseDetailsByHouseType
        ) =
            oldItem.displayId == newItem.displayId
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            LayoutNearbyListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        var currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem, options, iOnItemClickListener,activity)
        }
        holder.itemView.setOnClickListener {
            iOnItemClickListener.locationItemSelectedListener(currentItem)
        }

    }

    interface IOnItemClickListener {
        fun locationItemSelectedListener(model: HouseDetailsByHouseType)
        fun handleFavoriteAndUnFavorite(model: HouseDetailsByHouseType, isFavorite: Boolean)
    }

}