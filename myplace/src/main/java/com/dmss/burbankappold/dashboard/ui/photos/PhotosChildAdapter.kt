package com.dmss.burbankappold.dashboard.ui.photos

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dmss.burbankappold.databinding.ItemPhotosChildLayoutBinding
import com.dmss.burbankappold.databinding.ItemPhotosHeaderLayoutBinding
import com.dmss.burbankappold.databinding.ItemPhotosLayoutBinding
import com.dmss.burbankappold.utils.*
import models.photos.PhotoItem
import models.photos.PhotosDataItem

class PhotosChildAdapter(private val list: List<PhotosDataItem>?, val callBack : (PhotosDataItem) -> Unit)
    : RecyclerView.Adapter<PhotosChildAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPhotosChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return list?.size?:0
    }

    inner class ViewHolder(val binding: ItemPhotosChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photos: PhotosDataItem) {
            binding.root.setOnClickListener {
                callBack.invoke(photos)
            }
            println("photos.url:: ${photos.url.replace("=0", "")}")
            Glide.with(binding.root.context)
                .load("${photos.url.replace("=0", "")}")
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressbar.hide()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressbar.hide()
                        return false
                    }

                })
                .into(binding.imgPhoto)
        }
    }
}