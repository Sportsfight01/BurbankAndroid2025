package com.dmss.burbankapp.ui.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.NewHomeListModel
import com.dmss.burbankapp.databinding.ListCollectionItemBinding
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import timber.log.Timber
import kotlin.math.roundToInt


class CollectionsPlaceAdapter(
    var context: Context,
    var activity: Activity,
    var newHomesList: ArrayList<NewHomeListModel>,
    var selectItemClick: PackageItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var options: RequestOptions? = null
    private var customSharedPreferences: CustomSharedPreferences

    init {
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

        customSharedPreferences = CustomSharedPreferences(context)

    }

    interface PackageItemClick {
        fun selectedPackageItem(newHomeListModel: NewHomeListModel,index:Int)
        fun selectFavoriteItemClick(newHomeListModel: NewHomeListModel, isFavorite: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListCollectionItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PackageViewHolder(binding)

    }

    class PackageViewHolder(binding: ListCollectionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: ListCollectionItemBinding = binding


        fun bind(
            context: Context,
            newHomeListModel: NewHomeListModel,
            selectItemClick: PackageItemClick,
            options: RequestOptions?,
            customSharedPreferences: CustomSharedPreferences,
            activity: Activity
        ) {
            binding.tvPlace.text = (newHomeListModel.HouseName + " " + newHomeListModel.HouseSize)

            var price: String = ""
            newHomeListModel.Price?.roundToInt()?.let {
                price =
                    AppUtils.getCommasForPriceValue(it)
            }

            var placePicture: String? = newHomeListModel.facadePermantUrl

            newHomeListModel.facadePermantUrl?.let {
                if (it.contains("~")) {
                    placePicture = it.replace("~", "")
                }

                placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"

                GlideImageLoader(binding.ivPlace, binding.placeProgress).load(placePicture, options)

            }




            binding.tvPrice.text = ("$$price")
            binding.tvPrice.visibility = View.GONE
            binding.tvBath.text = (newHomeListModel.BathRooms.toString())
            binding.tvBed.text = (newHomeListModel.BedRooms.toString())
            binding.tvcar.text = (newHomeListModel.CarSpace.toString())


            newHomeListModel.MinLotWidth?.let {
                binding.tvWidth.text = (AppUtils.roundRemainingString(it.toString()) + "m")
            }

            binding.rlFirst.setOnClickListener {
                selectItemClick.selectedPackageItem(newHomeListModel,adapterPosition)
            }
            binding.rlSecond.setOnClickListener {
                selectItemClick.selectedPackageItem(newHomeListModel,adapterPosition)
            }
            newHomeListModel.IsFav.let {
                if (it) {
                    binding.ivLike.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.fav_selected
                        )
                    )
                } else
                    binding.ivLike.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.heart_bottom_svg
                        )
                    )
            }

            binding.ivLike.setOnClickListener {
                var isUserLoggedIn = customSharedPreferences.getUserLogin()

                if (isUserLoggedIn) {
                    if (newHomeListModel.IsFav) {
                        binding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.heart_bottom_svg
                            )
                        )
                    } else
                        binding.ivLike.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.fav_selected
                            )
                        )
                    newHomeListModel.IsFav.let {
                        Timber.e("Click Interface-----> ${newHomeListModel.IsFav}")
                        selectItemClick.selectFavoriteItemClick(newHomeListModel, !it)
                    }
                } else {
                    AppUtils.showPleaseLoginDialog(
                        context,
                        activity,
                        "Please login to add favourites"
                    )
                }


            }


        }
    }

    override fun getItemCount(): Int {
        return newHomesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is PackageViewHolder -> {
                holder.bind(
                    context,
                    newHomesList[position],
                    selectItemClick,
                    options,
                    customSharedPreferences,
                    activity
                )
            }

        }
    }

}


