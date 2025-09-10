package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.dmss.burbankappold.databinding.ItemHistoryLayoutBinding
import com.dmss.burbankappold.utils.*
import common.AppController
import java.util.*

class MyHistoryAdapter(val callBack : (NotesData) -> Unit) : RecyclerView.Adapter<MyHistoryAdapter.ViewHolder>() {
    var list = listOf<NotesData>()

    fun setMyHistoryData(list: List<NotesData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemHistoryLayoutBinding.inflate(
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

    inner class ViewHolder(val binding: ItemHistoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotesData) {
            binding.tvName.text = "By "+item.unknownAuthor
            binding.tvDesc.text = item.body?: "NA"
            binding.tvTitle.text = item.subject?: "NA"
            /*var username="--"
            if(AppController.controller?.userProfile!!.userDetails[0]!=null) {
                username = AppController.controller?.userProfile!!.userDetails[0].fullName
            }*/

            binding.tvDateTime.text = item.activityDate?: "NA"
            if(item.body==null || item?.body=="") {
                binding.tvDesc.text =    binding.root.context.getString(R.string.no_data)
            }
            if(item.unknownAuthor==null || item?.unknownAuthor=="") {
                binding.tvName.text = "By "+ binding.root.context.getString(R.string.no_data)
            }
          /*  binding.tvName.text = "By "+ username.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }*/
            if(item.subject==null || item?.subject=="") {
                binding.tvTitle.text =  binding.root.context.getString(R.string.no_data)
            }

            if(item.activityDate==null || item?.activityDate==""){
                binding.tvDateTime.text =   binding.root.context.getString(R.string.no_data)
            }else {
                binding.tvDateTime.text = item.activityDate!!.convertDateFormat(ACTUAL_DATE_FORMAT)
            }
        }
    }
}