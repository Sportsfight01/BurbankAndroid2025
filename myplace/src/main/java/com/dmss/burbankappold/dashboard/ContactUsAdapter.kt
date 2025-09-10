package com.dmss.burbankappold.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.databinding.ItemContactsLayoutBinding
import com.dmss.burbankappold.databinding.ItemDocumentsBinding
import com.dmss.burbankappold.databinding.ItemHistoryLayoutBinding
import com.dmss.burbankappold.databinding.ItemPhotosLayoutBinding
import com.dmss.burbankappold.utils.DOCUMENTS_FORMAT
import com.dmss.burbankappold.utils.convertDateFormat
import com.dmss.burbankappold.utils.loadUrl
import com.dmss.burbankappold.utils.photosBaseUrl
import kotlinx.coroutines.flow.combine
import models.contacts.MyContactItem
import models.history.MyHistoryItem
import models.photos.PhotosDataItem

class ContactUsAdapter(val callBack : (Int, MyContactItem) -> Unit) : RecyclerView.Adapter<ContactUsAdapter.ViewHolder>() {
    var list = listOf<MyContactItem>()

    fun setMyContactsData(list: List<MyContactItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemContactsLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.binding.imgMail.setOnClickListener {
            if (item.email.isNotEmpty()) callBack.invoke(0, item)
        }
        holder.binding.imgCall.setOnClickListener {
            if (item.phone.isNotEmpty()) callBack.invoke(1, item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemContactsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyContactItem) {
            binding.tvDesignation.text = item.designation
            binding.tvName.text = item.name
            binding.tvEmail.text = item.email
            binding.tvPhoneNo.text = item.phone.ifEmpty { "--" }
            binding.imgCall.alpha = if (item.phone.isEmpty()) 0.5f else 1f
            binding.imgMail.alpha = if (item.email.isEmpty()) 0.5f else 1f
        }
    }
}