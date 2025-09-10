package com.dmss.burbankapp.ui.designs.mycollectionfavorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.model.NewHomeListModel
import com.dmss.burbankapp.data.model.UserInfoModel
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import kotlin.math.roundToInt

class MyCollectionFavoritesAdapter(
    var context: Context,
    var newhomelist: ArrayList<NewHomeListModel>,
    var favoriteItemInterface: FavoritePackageItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var options: RequestOptions? = null
    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    var userInfoModel: UserInfoModel? = null

    interface FavoritePackageItemClick {
        fun selectedFavoriteDetailItem(newHomeListModel: NewHomeListModel,position: Int)
        fun unFavoriteItemClick(newHomeListModel: NewHomeListModel)
    }

    init {
        userInfoModel = CustomSharedPreferences.instance.getUserInfoModel()
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

    }

    //list_collection_item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            PackageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_collection_item, parent, false)
            )
        } else {
            HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.favorite_item_header, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PackageViewHolder -> {
                holder.bind(
                    context,
                    newhomelist[position],
                    favoriteItemInterface,
                    options,
                    userInfoModel
                )
            }
            is HeaderViewHolder -> {
                holder.bind(newhomelist[position], newhomelist)
            }

        }
    }

    override fun getItemCount(): Int = newhomelist.size

    override fun getItemViewType(position: Int): Int {
        if (newhomelist[position].message != null) {
            return TYPE_HEADER
        }
        return TYPE_ITEM
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header: TextView = itemView.findViewById(R.id.tv_header)
        fun bind(
            hnLQuizPackageModel: NewHomeListModel, packagesList: ArrayList<NewHomeListModel>
        ) {

            header.text = (hnLQuizPackageModel.message)

        }
    }

    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tv_place: TextView = itemView.findViewById(R.id.tv_place)
        var tv_price: TextView = itemView.findViewById(R.id.tv_price)

        var tv_bed: TextView = itemView.findViewById(R.id.tv_bed)
        var tv_bath: TextView = itemView.findViewById(R.id.tv_bath)
        var tvcar: TextView = itemView.findViewById(R.id.tvcar)
        var tv_width: TextView = itemView.findViewById(R.id.tv_width)
        var rl_first: RelativeLayout = itemView.findViewById(R.id.rl_first)
        var iv_like: ImageView = itemView.findViewById(R.id.iv_like)
        var iv_place: ImageView = itemView.findViewById(R.id.iv_place)
        var place_progress: ProgressBar = itemView.findViewById(R.id.place_progress)


        fun bind(
            context: Context,
            newHomeListModel: NewHomeListModel,
            favoriteItemInterface: FavoritePackageItemClick,
            options: RequestOptions?, userInfoModel: UserInfoModel?
        ) {
            itemView.setOnClickListener {
                var model = NewHomeListModel(
                    newHomeListModel.facadePermantUrl,
                    newHomeListModel.HouseName,
                    newHomeListModel.HouseSize,
                    newHomeListModel.Price,
                    newHomeListModel.BedRooms,
                    newHomeListModel.BathRooms,
                    newHomeListModel.Storey,
                    newHomeListModel.CarSpace,
                    newHomeListModel.MinLotWidth,
                    true,
                    newHomeListModel.message,
                    newHomeListModel.Id,
                    newHomeListModel.houseIdLandBank
                )
                favoriteItemInterface.selectedFavoriteDetailItem(model,adapterPosition)
            }

            var price: String = ""
            newHomeListModel.Price?.roundToInt()?.let {
                price =
                    AppUtils.getCommasForPriceValue(it)
            }
            tv_place.text = (newHomeListModel.HouseName + " " + newHomeListModel.HouseSize)
            if (price.isNotEmpty()) {
                tv_price.text = ("$$price")
            }
            tv_price.visibility = View.GONE
            tv_bath.text = (newHomeListModel.BathRooms.toString())
            tv_bed.text = (newHomeListModel.BedRooms.toString())
            tvcar.text = (newHomeListModel.CarSpace.toString())


            if (userInfoModel != null) {
                if (userInfoModel.FirstName?.let { newHomeListModel.currentUserName?.contains(it) } == true) {
                    iv_like.visibility = View.VISIBLE
                } else {
                    iv_like.visibility = View.GONE
                }
            }

            /*newHomeListModel.MinLotWidth?.let {
                tv_width.text = String.format("%.1fm", it)
            }*/
            newHomeListModel.MinLotWidth?.let {
                tv_width.text = (AppUtils.roundRemainingString(it.toString()) + "m")
            }
            iv_like.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.fav_selected
                )
            )
            iv_like.setOnClickListener {
                favoriteItemInterface.unFavoriteItemClick(newHomeListModel)
            }
            var placePicture: String? = newHomeListModel.facadePermantUrl

            newHomeListModel.facadePermantUrl?.let {
                if (it.contains("~")) {
                    placePicture = it.replace("~", "")
                }

                placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"

                GlideImageLoader(iv_place, place_progress).load(placePicture, options)

            }


        }

    }

    fun setAndUpdateData(dataList: ArrayList<NewHomeListModel>) {
        newhomelist.clear()
        newhomelist.addAll(dataList)
        notifyDataSetChanged()
    }

}