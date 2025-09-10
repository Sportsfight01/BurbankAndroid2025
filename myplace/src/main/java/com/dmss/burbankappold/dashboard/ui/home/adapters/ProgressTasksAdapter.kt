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
import com.dmss.burbankappold.dashboard.ui.home.ProgressDetailsFragment.Companion.getStageColor
import com.dmss.burbankappold.dashboard.ui.home.ProgressStage
import com.dmss.burbankappold.databinding.ItemProgressDetailsTaskBinding
import com.dmss.burbankappold.utils.convertDateFormat
import models.progress.ProgressDetailsTasks

class ProgressTasksAdapter(val list: List<ProgressDetailsTasks>) : RecyclerView.Adapter<ProgressTasksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemProgressDetailsTaskBinding.inflate(
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

    inner class ViewHolder(val binding: ItemProgressDetailsTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(progressDetailsTasks: ProgressDetailsTasks) {
            val context = binding.root.context
            binding.tvTitle.text = progressDetailsTasks.name
            when(progressDetailsTasks.status){
                context.getString(R.string.completed) ->{
                    if(progressDetailsTasks.date!=null && progressDetailsTasks.date!=""){
                        var dateTime=progressDetailsTasks.date.convertDateFormat()
                        binding.tvDate.text = dateTime
                        binding.ivStatus.changeImageIcon(context, getStageColor(progressDetailsTasks.stageName!!))

                    }else{
                        binding.tvDate.text = "--"

                    }
                }
                else ->{
                    binding.ivStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unchecked))
//                    binding.tvDate.text = "--"
                    if(progressDetailsTasks.date!=null && progressDetailsTasks.date!=""){
                        var dateTime=progressDetailsTasks.date.convertDateFormat()
                        binding.tvDate.text = dateTime
                        binding.ivStatus.changeImageIcon(context, getStageColor(progressDetailsTasks.stageName!!))

                    }else{
                        binding.tvDate.text = "--"

                    }
                }
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