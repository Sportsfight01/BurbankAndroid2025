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
import com.dmss.burbankappold.databinding.ItemPhotosHeaderLayoutBinding
import com.dmss.burbankappold.databinding.ItemPhotosLayoutBinding
import com.dmss.burbankappold.utils.*
import models.photos.PhotoItem
import models.photos.PhotosDataItem
import java.sql.SQLOutput
import java.util.*

class PhotosHeaderAdapter(private val list: List<List<PhotosDataItem>>?, val callBack : (PhotosDataItem) -> Unit) : RecyclerView.Adapter<PhotosHeaderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPhotosHeaderLayoutBinding.inflate(
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

    inner class ViewHolder(val binding: ItemPhotosHeaderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(list1: List<PhotosDataItem>) {
            var list= sortBydateList(list1)
            var listtitle= list

            val photos = list[0]
            val photostitle = listtitle[0]

            /* binding.tvTitle.text = photos.title.lowercase().replaceFirstChar {
                 if (it.isLowerCase()) it.titlecase(
                     Locale.getDefault()
                 ) else it.toString()
             }*/
            var title=photostitle.title.lowercase()
            binding.tvTitle.text = title.sentenceFirstLetterCaps(title).toString()
            binding.tvPhotoDate.text = photos.metaData.createdOn.convertDateFormat(PHOTOS_DATE_FORMAT)
            binding.rvHeader.apply {
                this.layoutManager = GridLayoutManager(binding.root.context, 5)
                var filterList=sortBydateList(list)

                this.adapter = PhotosChildAdapter(filterList){
                    callBack.invoke(it)
                }
            }
        }
    }
    private fun sortBydateList(documentsList : List<PhotosDataItem>):List<PhotosDataItem>{
        val sortedList = documentsList.sortedByDescending {

            it.metaData.createdOn }
        return sortedList
    }
}