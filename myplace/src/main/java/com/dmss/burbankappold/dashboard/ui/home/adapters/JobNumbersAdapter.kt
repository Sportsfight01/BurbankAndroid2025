package com.dmss.burbankappold.dashboard.ui.home.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.home.ProgressDetailsFragment
import com.dmss.burbankappold.databinding.ItemProgressDetailsTaskBinding
import com.dmss.burbankappold.databinding.JobnmbersItemBinding
import com.dmss.burbankappold.utils.convertDateFormat
import common.AppController
import models.progress.ProgressData
import models.progress.ProgressDetailsTasks

class JobNumbersAdapter (val list: List<String>,val callBack : (String) -> Unit) : RecyclerView.Adapter<JobNumbersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = JobnmbersItemBinding.inflate(
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

    inner class ViewHolder(val binding: JobnmbersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(progressDetailsTasks: String) {
            val context = binding.root.context
            var PREVIOUSSELECTEDJOBNUMBER= AppController.getPreference(context, AppController.PREVIOUSSELECTEDJOBNUMBER)
            println("PREVIOUSSELECTEDJOBNUMBER$PREVIOUSSELECTEDJOBNUMBER")
            if(progressDetailsTasks == PREVIOUSSELECTEDJOBNUMBER){
                binding.tvJobnumber.background=context.getDrawable(R.drawable.rectangle_orange_bg)
                binding.tvJobnumber.setTextColor( ContextCompat.getColor(
                    context,
                    R.color.white_3_1
                ))
            }

            binding.tvJobnumber.text = progressDetailsTasks
            binding.root.setOnClickListener{
                callBack.invoke(progressDetailsTasks)
            }
        }

    }

    fun ImageView.changeImageIcon(context: Context, colorID: Int){
        val mIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_checked)
        mIcon?.setColorFilter(
            ContextCompat.getColor(context, colorID),
            PorterDuff.Mode.SRC_IN
        )
        this.setImageDrawable(mIcon)
    }
}