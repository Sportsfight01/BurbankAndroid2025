package com.dmss.burbankappold.dashboard.ui.photos

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dmss.burbankappold.R
import com.dmss.burbankappold.utils.PHOTOS_DATE_FORMAT
import com.dmss.burbankappold.utils.convertDateFormat
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.photosBaseUrl
//import com.smarteist.autoimageslider.SliderViewAdapter

// on below line we are creating a class for slider
// adapter and passing our array list to it.
class SliderAdapter(imageUrl: List<String>,dates: List<String>,titles: List<String>,imageFormatType:String) {
    /*:
    SliderViewAdapter<SliderAdapter.SliderViewHolder>() {

    // on below line we are creating a
    // new array list and initializing it.
    var sliderList: List<String> = imageUrl
    var imageFormatType: String = imageFormatType
    var dates: List<String> = dates
    var titles: List<String> = titles

    // on below line we are calling get method
    override fun getCount(): Int {
        // in this method we are returning
        // the size of our slider list.
        return sliderList.size
    }

    // on below line we are calling on create view holder method.
    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapter.SliderViewHolder {
        // inside this method we are inflating our layout file for our slider view.
        val inflate: View =
            LayoutInflater.from(parent!!.context).inflate(R.layout.slider_item, null)

        // on below line we are simply passing
        // the view to our slider view holder.
        return SliderViewHolder(inflate)
    }

    // on below line we are calling on bind view holder method to set the data to our image view.
    override fun onBindViewHolder(viewHolder: SliderAdapter.SliderViewHolder?, position: Int) {

        // on below line we are checking if the view holder is null or not.
        if (viewHolder != null) {
            // if view holder is not null we are simply
            // loading the image inside our image view using glide library
            Glide.with(viewHolder.itemView)
                .load("${sliderList[position]?.replace("=0", "")}")
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewHolder.progressbar.hide()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewHolder.progressbar.hide()
                        return false
                    }

                })
//                .load(sliderList[position]).fitCenter()
                .into(viewHolder.imageView)
            viewHolder.title.text=titles[position].capitalize()
            viewHolder.date.text=dates[position].convertDateFormat(PHOTOS_DATE_FORMAT)
            println("position333:: "+position+" dates.size:: "+dates.size)

            if(position==dates.size-1){
                println("position111:: "+position+" dates.size:: "+dates.size)
                viewHolder.right_arrow.visibility=View.GONE
                viewHolder.left_arrow.visibility=View.VISIBLE
            }
            else if(position==0){
                println("position222:: "+position+" dates.size:: "+dates.size)

                viewHolder.right_arrow.visibility=View.VISIBLE
                viewHolder.left_arrow.visibility=View.GONE
            }
        }
    }

    // on below line we are creating a class for slider view holder.
    class SliderViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {

        // on below line we are creating a variable for our
        // image view and initializing it with image id.
        var imageView: ImageZoom = itemView!!.findViewById(R.id.myimage)
        var title: TextView = itemView!!.findViewById(R.id.title)
        var date: TextView = itemView!!.findViewById(R.id.date)
        var progressbar: ProgressBar = itemView!!.findViewById(R.id.progressbar)
        var left_arrow: ImageView = itemView!!.findViewById(R.id.left_arrow)
        var right_arrow: ImageView = itemView!!.findViewById(R.id.right_arrow)

    }*/
}