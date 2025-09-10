package com.dmss.burbankapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import com.dmss.burbankapp.R
import com.dmss.burbankapp.utils.AppConstants
import com.squareup.picasso.Picasso

class SlidingAdapter(var context: Context, var images: ArrayList<String>) : PagerAdapter() {

    lateinit var layoutInflater: LayoutInflater


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = LayoutInflater.from(container.context).inflate(
            R.layout.slider_image_item, container,
            false
        )
        var imageView: ImageView = item.findViewById(R.id.iv_slide)
        var progress_circular: ProgressBar = item.findViewById(R.id.progress_circular)
        progress_circular.visibility = View.VISIBLE

        var imageUrl = images[position]

        var facadeImageUrl: String = ""

        if (imageUrl.contains("~")) {
            imageUrl =
                imageUrl.replace("~", "")
            facadeImageUrl = "${AppConstants.WEB_BASE_URL}${imageUrl}"

        } else {
            facadeImageUrl = imageUrl
        }

        Picasso.get().load(facadeImageUrl).error(R.mipmap.logo_myplace)
            .into(imageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    progress_circular.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progress_circular.visibility = View.GONE
                    imageView.setBackgroundResource(R.drawable.rectangle_test)
                }


            })
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