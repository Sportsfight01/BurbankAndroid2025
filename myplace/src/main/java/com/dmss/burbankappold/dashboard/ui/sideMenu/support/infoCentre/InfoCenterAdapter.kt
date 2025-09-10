package com.dmss.burbankappold.dashboard.ui.sideMenu.support.infoCentre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.ItemDocumentsBinding
import com.dmss.burbankappold.databinding.ItemHistoryLayoutBinding
import com.dmss.burbankappold.databinding.ItemInfoCenterLayoutBinding
import com.dmss.burbankappold.databinding.ItemPhotosLayoutBinding
import com.dmss.burbankappold.utils.*
import common.Common
import kotlinx.coroutines.flow.combine
import models.faq.FAQDataItem
import models.history.MyHistoryItem
import models.infoCentre.InfoCenterData
import models.photos.PhotosDataItem
import okhttp3.internal.notify

class InfoCenterAdapter(val callBack : (FAQDataItem) -> Unit) : RecyclerView.Adapter<InfoCenterAdapter.ViewHolder>() {
    var mInfoList = listOf<FAQDataItem>()
    fun setInfoList(list: List<FAQDataItem>){
        mInfoList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemInfoCenterLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mInfoList[position])
    }

    override fun getItemCount() = mInfoList.size

    inner class ViewHolder(val binding: ItemInfoCenterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FAQDataItem) {
            binding.tvTitle.text = item.Heading
            Glide.with(binding.root.context)
                .load("${Common.INFO_IMAGE_BASE_URL}${item.Image}")
                .into(binding.infoImage)
            binding.root.setOnClickListener{callBack.invoke(item)}
        }
    }
}