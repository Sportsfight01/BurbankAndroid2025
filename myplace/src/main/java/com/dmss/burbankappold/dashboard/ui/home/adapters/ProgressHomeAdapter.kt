package com.dmss.burbankappold.dashboard.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.home.ProgressStage
import com.dmss.burbankappold.databinding.ItemProgressLayoutBinding
import com.dmss.burbankappold.utils.convertDateFormat
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import models.progress.ProgressData

class ProgressHomeAdapter(val callBack : (ProgressData) -> Unit) : RecyclerView.Adapter<ProgressHomeAdapter.ViewHolder>() {
    var list = arrayListOf<ProgressData>()
    fun setProgressList(list: ArrayList<ProgressData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemProgressLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(list[position])
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemProgressLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(progressData: ProgressData) {
            val context = binding.root.context
            binding.tvSeeMore.visibility =
                if (adapterPosition == 0) View.INVISIBLE else View.VISIBLE
            val clk_rotate =  AnimationUtils.loadAnimation(
                context,
                R.anim.progress_rotate
            )

            // assigning that animation to
            // the image and start animation
            binding.progress.startAnimation(clk_rotate)
            binding.tvName.text = progressData.name
            if (adapterPosition == 0) {
                binding.tvDate.text = "Last update ${progressData.date?.convertDateFormat()}"
                var progresstext=""
                if (progressData.progress == 100){
                    progresstext= binding.root.context.getString(R.string.complete)
                }else {
                    progresstext=binding.root.context.getString(R.string.progress_percentage, "${progressData.progress}%")
                    if(progressData.progress == 0){
                        progresstext=  "Pending"

                    }

                }
                binding.tvProgress.text =progresstext
            } else {
                if (progressData.progress == 100) {
                    binding.tvDate.text = "Completed ${progressData.date?.convertDateFormat()}"
                    binding.tvProgress.text = binding.root.context.getString(
                        R.string.complete
                    )
                } else {
                    if (progressData.tasksCompleted == 0) {
                        binding.tvDate.text = "${progressData.totalTasks} Tasks to Complete"
                        binding.tvProgress.text = "Pending"
                    } else {
                        binding.tvDate.text =
                            "${progressData.tasksCompleted} of ${progressData.totalTasks} Tasks Complete"
                        binding.tvProgress.text = binding.root.context.getString(
                            R.string.progress_percentage,
                            "${progressData.progress}%"
                        )
                    }
                }
            }
            binding.tvDate.visibility = View.VISIBLE
            when (progressData.progressStage) {
                ProgressStage.AdminStage -> {
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_admin)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_admin_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_admin))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_admin_stage))
                    binding.tvDescription.text = context.getString(R.string.home_admin_small_desc)+"\n  "
                }
                ProgressStage.BaseStage ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_base)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_base_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_base_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_base_stage))
                    binding.tvDescription.text = context.getString(R.string.home_base_small_desc)+"\n  "
                }
                ProgressStage.FrameStage ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_frame)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_frame_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_frame_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_frame_stage))
                    binding.tvDescription.text = context.getString(R.string.home_frame_small_desc)+"\n  "
                }
                ProgressStage.LockupStage ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_lockup)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_lockup_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_lockup_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_lockup_stage))
                    binding.tvDescription.text = context.getString(R.string.home_lockup_small_desc)+"\n  "
                }
                ProgressStage.FixingStage ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_fixing)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_fixing_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_fixing_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_fixing_stage))
                    binding.tvDescription.text = context.getString(R.string.home_fixing_small_desc)+"\n  "
                }
                ProgressStage.FinishingStage ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_finishing)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_finishing_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_finishing_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_final_stage))
                    binding.tvDescription.text = context.getString(R.string.home_finishing_small_desc)+"\n  "
                }
                else ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.appColor))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.appColor))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home))
                    binding.tvDate.visibility = View.INVISIBLE
                    binding.tvDescription.text = context.getString(R.string.home_new_home_small_desc)
                }
            }
            binding.progress.progress = progressData.progress
            /*binding.tvSeeMore.setOnClickListener {
                callBack.invoke(progressData)
            }*/
            binding.root.setOnClickListener{
                if(ProgressStage.HomStage!=progressData.progressStage) {
                    callBack.invoke(progressData)
                }

            }
        }
    }
}