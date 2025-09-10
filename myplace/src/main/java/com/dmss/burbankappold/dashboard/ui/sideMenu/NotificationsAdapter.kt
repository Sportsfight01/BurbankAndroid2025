package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.*
import com.dmss.burbankappold.utils.convertDateFormat
import com.dmss.burbankappold.utils.convertDateFormatNotification
import models.NotificationModel

class NotificationsAdapter(val callBack : (NotificationModel) -> Unit) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    var list = arrayListOf<NotificationModel>()

    fun setNotifications(list: ArrayList<NotificationModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = NotificationListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notificationModel: NotificationModel) {
            var displayString = ""
            if (notificationModel.isPhoto) {
                displayString = "Check out the new photos added on " + notificationModel.date.convertDateFormatNotification()
//                displayString = "Check out the new photos added on " + notificationModel.date.convertDateFormatCompare1()

                binding.notificationImageView.setImageResource(R.drawable.image)
            } else {
                displayString = if (notificationModel.isStage()) {
                    "\"" + notificationModel.heading + "\" changed from \"" + notificationModel.oldStage + "\" to \" " + notificationModel.currentStage + "\" on " + notificationModel.date + "."
                } else {
                    "\"" + notificationModel.heading + "\" Completed on " + notificationModel.date + "."
                }
                binding.notificationImageView.setImageResource(R.drawable.notification_filled)

            }
            binding.notificationTimeTextView.text = notificationModel.daysTag
            binding.notificationDataTextView.text = displayString
            binding.root.setOnClickListener {
                callBack.invoke(notificationModel)
            }
        }
    }
}