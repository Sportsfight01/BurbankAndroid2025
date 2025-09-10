package com.dmss.burbankapp.ui.cluster

import android.app.Activity
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
import com.dmss.burbankapp.ui.view.GlideImageLoader
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils

class HomeLandClusterAdapter(
    var context: Context,
    var activity:Activity,
    var packagesList: ArrayList<HnlClusterItem>,
    var selectItemClick: PackageItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var options: RequestOptions? = null
    private var customSharedPreferences: CustomSharedPreferences = CustomSharedPreferences(context)

    fun setData(dataList: ArrayList<HnlClusterItem>) {
        packagesList.clear()
        packagesList = dataList
        notifyDataSetChanged()

    }


    init {
        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)

    }

    interface PackageItemClick {
        fun selectedPackageItem(hnLQuizPackageModel: HnLQuizPackageModel)
        fun selectFavoriteItemClick(hnLQuizPackageModel: HnLQuizPackageModel, isFavorite: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PackageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_place, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return packagesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is PackageViewHolder -> {
                holder.bind(
                    context,
                    packagesList[position],
                    selectItemClick,
                    options,
                    customSharedPreferences,
                    activity
                )
            }

        }
    }

    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var housePrice: TextView = itemView.findViewById(R.id.header)
        var tv_area: TextView = itemView.findViewById(R.id.tv_area)
        var tv_bedrooms: TextView = itemView.findViewById(R.id.tv_bedrooms)
        var tv_bathrooms: TextView = itemView.findViewById(R.id.tv_bathrooms)
        var tv_carspaces: TextView = itemView.findViewById(R.id.tv_carspaces)
        var iv_place: ImageView = itemView.findViewById(R.id.iv_place)
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
        var iv_like: ImageView = itemView.findViewById(R.id.iv_like)
        var place_progress: ProgressBar = itemView.findViewById(R.id.place_progress)
        fun bind(
            context: Context,
            hnlClusterItem: HnlClusterItem,
            packageItemClick: PackageItemClick,
            options: RequestOptions?,
            customSharedPreferences: CustomSharedPreferences,
            activity: Activity
        ) {
            var hnLQuizPackageModel: HnLQuizPackageModel = hnlClusterItem.hnLQuizPackageModel

            val price: String? =
                hnLQuizPackageModel.price?.let { AppUtils.getCommasForPriceValue(it.toInt()) }
            housePrice.text = ("$$price")

            tv_area.text = hnLQuizPackageModel.address
            tv_bedrooms.text = hnLQuizPackageModel.bedrooms.toString()
            tv_bathrooms.text = hnLQuizPackageModel.bathrooms.toString()
            tv_carspaces.text = hnLQuizPackageModel.carspace.toString()
            // Changed by durga 11/10/2022
            tv_title.text = hnLQuizPackageModel.houseName +" "+hnLQuizPackageModel.houseSize
            if (hnLQuizPackageModel.isFav) {
                iv_like.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.fav_selected
                    )
                )
            } else
                iv_like.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.heart_bottom_svg
                    )
                )

            var placePicture: String? = hnLQuizPackageModel.FacadePermanentUrl
            if (hnLQuizPackageModel.FacadePermanentUrl?.contains("~") == true) {
                placePicture = hnLQuizPackageModel.FacadePermanentUrl?.replace("~", "")
            }
            placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"
            GlideImageLoader(iv_place, place_progress).load(placePicture, options)

            itemView.setOnClickListener {
                packageItemClick.selectedPackageItem(hnLQuizPackageModel)
            }


            iv_like.setOnClickListener {

                val isUserLoggedIn = customSharedPreferences.getUserLogin()

                if (isUserLoggedIn) {
                    if (hnLQuizPackageModel.isFav) {
                        iv_like.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.heart_bottom_svg
                            )
                        )
                    } else
                        iv_like.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.fav_selected
                            )
                        )

                    packageItemClick.selectFavoriteItemClick(
                        hnLQuizPackageModel,
                        !hnLQuizPackageModel.isFav
                    )
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


}