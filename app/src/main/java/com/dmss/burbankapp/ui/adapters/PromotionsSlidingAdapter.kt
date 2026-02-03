package com.dmss.burbankapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.model.PromotionItem
import com.dmss.burbankapp.data.model.PromotionsResponse
import com.dmss.burbankapp.utils.AppConstants
import com.squareup.picasso.Picasso


class PromotionsSlidingAdapter(var context: Context, var images: List<PromotionItem>, private val onItemClick: (String) -> Unit) : PagerAdapter() {

    lateinit var layoutInflater: LayoutInflater


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = LayoutInflater.from(container.context).inflate(
            R.layout.slider_image_item, container,
            false
        )
        var imageView: ImageView = item.findViewById(R.id.iv_slide)

        imageView.setOnClickListener { images[position]?.pageUrl?.let { it1 -> onItemClick(it1) }}

        var progress_circular: ProgressBar = item.findViewById(R.id.progress_circular)
//        progress_circular.visibility = View.VISIBLE
        var imageUrl: String =""
        images[position]?.let { imageUrl=it.image }
//        var imageUrl = images[position]
        var facadeImageUrl: String = ""

        if(imageUrl!=null) {

            if (imageUrl.contains("~")) {
                imageUrl =
                    imageUrl.replace("~", "")
                facadeImageUrl = "${AppConstants.WEB_BASE_URL}${imageUrl}"

            } else {
                facadeImageUrl = imageUrl
            }
        }

        val request = if (facadeImageUrl.isNullOrBlank()) {
            Picasso.get().load(R.drawable.promotions_image_not_available) // local drawable
        } else {
            Picasso.get().load(facadeImageUrl)
        }

        request
            .error(R.drawable.promotions_image_not_available)
            .fit()
            .into(imageView)

        /* Picasso.get().load(facadeImageUrl).error(R.mipmap.logo_myplace)
             .into(imageView, object : com.squareup.picasso.Callback {
                 override fun onSuccess() {
                     progress_circular.visibility = View.GONE
                 }

                 override fun onError(e: Exception?) {
                     progress_circular.visibility = View.GONE
                     imageView.setBackgroundResource(R.drawable.rectangle_test)
                 }


             })*/
        container.addView(item)
        return item
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}