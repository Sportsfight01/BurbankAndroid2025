package com.dmss.burbankappold.dashboard.ui.photos

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dmss.burbankappold.databinding.ItemPhotosLayoutBinding
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.loadUrl
import com.dmss.burbankappold.utils.photosBaseUrl
import com.dmss.burbankappold.utils.sentenceFirstLetterCaps
import models.photos.PhotosDataItem

class PhotosAdapter(val callBack : (Int) -> Unit) : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    var list = listOf<List<PhotosDataItem>>()

    fun setPhotosList(list: List<List<PhotosDataItem>>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPhotosLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemPhotosLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(list1: List<PhotosDataItem>) {
            var list= sortBydateList(list1)

            val photos = list[0]
            binding.imgPhotos.loadUrl(photos.url, photos.extension)
            binding.imgPhotos.clipToOutline = true
            var title=photos.title.lowercase()
            binding.tvTitle.text = title.sentenceFirstLetterCaps(title).toString()
            binding.tvPhotosCount.text = list.size.toString()
            binding.imgPhotos.setOnClickListener{
                callBack.invoke(adapterPosition)
            }
            Glide.with(binding.root.context)
                .load("${photos.url?.replace("=0", "")}")
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
                .into(binding.imgPhotos)
        }
    }
    private fun sortBydateList(documentsList : List<PhotosDataItem>):List<PhotosDataItem>{
        val sortedList = documentsList.sortedByDescending {  it.metaData.createdOn }
        return sortedList
    }
}