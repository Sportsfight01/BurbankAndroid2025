package com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.*
import com.dmss.burbankappold.utils.*
import common.AppController
import kotlinx.coroutines.flow.combine
import models.contactUs.ContactUsData
import models.contactUs.ContactUsDataItem
import models.history.MyHistoryItem
import models.infoCentre.InfoCenterData
import models.photos.PhotosDataItem

class ContactUsRepliesAdapter : RecyclerView.Adapter<ContactUsRepliesAdapter.ViewHolder>() {
    var mList = listOf<NotesData>()
    fun setContactsList(list: ArrayList<NotesData>){
        mList = list
        println("contactUsDataItem:: $list")

        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRepliesLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemRepliesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contactUsDataItem: NotesData) {
//            println("contactUsDataItem:: $contactUsDataItem")
            var title=""

            title = if(contactUsDataItem?.author?.fullName==null || contactUsDataItem?.author?.fullName==""){
                "--"
            }else{
                contactUsDataItem?.author?.fullName
            }
            var username="--"
            if(AppController.controller?.userProfile!!.userDetails[0]!=null) {
                username = AppController.controller?.userProfile!!.userDetails[0].fullName
            }
            if(contactUsDataItem?.createdInMyHome){
//                binding.tvTitle.text = username+" - "+contactUsDataItem.noteId
                binding.tvTitle.text = username

            }else {
//                println("contactUsDataItem?.createdInMyHome  "+ contactUsDataItem.createdInMyHome+"  "+contactUsDataItem?.unknownAuthor)
//                binding.tvTitle.text = title+" - "+contactUsDataItem.noteId+" - "+binding.root.context.getString(R.string.burbank_homes)
                binding.tvTitle.text = title+" - "+binding.root.context.getString(R.string.burbank_homes)

            }


            binding.tvMessage.text = contactUsDataItem.body

            binding.tvReplyDate.text = contactUsDataItem.activityDate?.convertDateFormat(
                ACTUAL_DATE_FORMAT)
        }
    }
}