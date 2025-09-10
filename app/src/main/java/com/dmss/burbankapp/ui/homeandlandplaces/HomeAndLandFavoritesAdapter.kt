package com.dmss.burbankapp.ui.homeandlandplaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.HnLQuizPackageModel
import com.dmss.burbankapp.data.model.UserInfoModel
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import kotlin.math.roundToInt

class HomeAndLandFavoritesAdapter(
    var context: Context,
    var packagesList: ArrayList<HnLQuizPackageModel>,
    var favoriteItemInterface: FavoritePackageItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var options: RequestOptions? = null
    var userInfoModel: UserInfoModel? = null

    init {
        userInfoModel = CustomSharedPreferences.instance.getUserInfoModel()
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)
    }

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            PackageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_place, parent, false)
            )
        } else {
            HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.favorite_item_header, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return packagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (packagesList[position].message != null) {
            return TYPE_HEADER
        }
        return TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PackageViewHolder -> {
                holder.bind(
                    context,
                    packagesList[position],
                    favoriteItemInterface,
                    options,
                    userInfoModel
                )
            }
            is HeaderViewHolder -> {
                holder.bind(packagesList[position], packagesList)
            }

        }

    }


    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header: TextView = itemView.findViewById(R.id.tv_header)
        fun bind(
            hnLQuizPackageModel: HnLQuizPackageModel,
            packagesList: ArrayList<HnLQuizPackageModel>
        ) {

            header.text = (hnLQuizPackageModel.message)

        }
    }

    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var housePrice: TextView = itemView.findViewById(R.id.header)
        private var tv_area: TextView = itemView.findViewById(R.id.tv_area)
        private var tv_bedrooms: TextView = itemView.findViewById(R.id.tv_bedrooms)
        var tv_bathrooms: TextView = itemView.findViewById(R.id.tv_bathrooms)
        var tv_carspaces: TextView = itemView.findViewById(R.id.tv_carspaces)
        var iv_place: ImageView = itemView.findViewById(R.id.iv_place)
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
        var iv_like: ImageView = itemView.findViewById(R.id.iv_like)
        var place_progress: ProgressBar = itemView.findViewById(R.id.place_progress)
        fun bind(
            context: Context,
            hnLQuizPackageModel: HnLQuizPackageModel,
            favoriteItemInterface: FavoritePackageItemClick,
            options: RequestOptions?,
            userInfoModel: UserInfoModel?
        ) {
            itemView.setOnClickListener {
                favoriteItemInterface.selectedFavoriteDetailItem(hnLQuizPackageModel)
            }

            var price: String = ""
            hnLQuizPackageModel.price?.roundToInt().let {
                price =
                    it?.let { it1 -> AppUtils.getCommasForPriceValue(it1) }.toString()
            }


            housePrice.text = ("$$price")
            tv_area.text = hnLQuizPackageModel.address
            tv_bedrooms.text = hnLQuizPackageModel.bedrooms.toString()
            tv_bathrooms.text = hnLQuizPackageModel.bathrooms.toString()
            tv_carspaces.text = hnLQuizPackageModel.carspace.toString()
            tv_title.text = ("${hnLQuizPackageModel.houseName}  ${hnLQuizPackageModel.houseSize}")


            if (userInfoModel != null) {
                if (userInfoModel.FirstName?.let { hnLQuizPackageModel.currentUserName?.contains(it) } == true) {
                    iv_like.visibility = View.VISIBLE
                } else {
                    iv_like.visibility = View.GONE
                }
            }


            iv_like.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.fav_selected
                )
            )
            iv_like.setOnClickListener {
                favoriteItemInterface.unFavoriteItemClick(hnLQuizPackageModel)
            }
            var placePicture: String? = hnLQuizPackageModel.FacadePermanentUrl
            hnLQuizPackageModel.FacadePermanentUrl?.contains("~")?.let {
                placePicture = hnLQuizPackageModel.FacadePermanentUrl?.replace("~", "")
            }
            placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"
            GlideImageLoader(iv_place, place_progress).load(placePicture, options)


        }

    }

    interface FavoritePackageItemClick {
        fun selectedFavoriteDetailItem(hnLQuizPackageModel: HnLQuizPackageModel)
        fun unFavoriteItemClick(hnLQuizPackageModel: HnLQuizPackageModel)
    }

    public fun setAndUpdateData(dataList: ArrayList<HnLQuizPackageModel>) {
        packagesList.clear()
        packagesList.addAll(dataList)
        notifyDataSetChanged()
    }
}