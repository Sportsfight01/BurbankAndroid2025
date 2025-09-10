package com.dmss.burbankappold.dashboard.ui.home.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.home.ProgressStage
import com.dmss.burbankappold.databinding.ItemProgressLayoutBinding
import com.dmss.burbankappold.utils.AppConstants
import com.dmss.burbankappold.utils.convertDateFormat
import common.AppController
import models.photos.PhotosDataItem
import models.progress.*
import java.math.RoundingMode

class ProgressHomeNewAdapter(val callBack : (ProgressData) -> Unit) : RecyclerView.Adapter<ProgressHomeNewAdapter.ViewHolder>() {
    /*    var list = arrayListOf<ProgressNewData>()
    //    lateinit var constructionContractData:ConstructionContract
    //    lateinit var preconstructionContractData:PreconstructionContract
        lateinit var constructionContractprogressNewData:Map<String,Int>
        lateinit var preconstructionContractprogressNewData:Map<String,Int>
        var listPreOfCompletedTasks =ArrayList<TaskCompletedData>()
        var listPreOfPreConstructionTasks =ArrayList<TaskCompletedData>()*/
    var listConstructionStageCondition =ArrayList<TaskCompletedInfoData>()

    /*  fun setProgressList(list: ArrayList<ProgressNewData>, constructionContractprogressNewData:Map<String,Int>,
                        preconstructionContractprogressNewData:Map<String,Int>,listPreOfCompletedTasks:ArrayList<TaskCompletedData>,
                        listPreOfPreConstructionTasks :ArrayList<TaskCompletedData>
                        ) {
        this.list = list
        this.constructionContractprogressNewData=constructionContractprogressNewData
        this.preconstructionContractprogressNewData=preconstructionContractprogressNewData
        this.listPreOfCompletedTasks=listPreOfCompletedTasks
        this.listPreOfPreConstructionTasks=listPreOfPreConstructionTasks

        notifyDataSetChanged()
    }*/
    @SuppressLint("SuspiciousIndentation")
    fun setProgressList(listConstructionStageCondition: ArrayList<TaskCompletedInfoData>) {
        this.listConstructionStageCondition = listConstructionStageCondition
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
        println("listConstructionStageCondition SIZEE::"+listConstructionStageCondition.size)
        if(listConstructionStageCondition.size>0 && listConstructionStageCondition[position]!=null) {
            holder.bind(listConstructionStageCondition[position])
        }
    }


    override fun getItemCount(): Int {
        return listConstructionStageCondition.size
    }

    inner class ViewHolder(val binding: ItemProgressLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(progressData: TaskCompletedInfoData) {
            val context = binding.root.context
            binding.tvSeeMore.visibility =
                if (adapterPosition == 0) View.INVISIBLE else View.VISIBLE
            val clk_rotate =  AnimationUtils.loadAnimation(
                context,
                R.anim.progress_rotate
            )
            var percentage =0
            // assigning that animation to
            // the image and start animation
            binding.progress.startAnimation(clk_rotate)
            binding.tvName.text = progressData.stageName
            var completedStageCount=progressData.noOfCompletedTasks
            var totalStages = progressData.noOfTasks
            if (adapterPosition == 0) {
//                binding.tvDate.text = "Last update ${progressData.date?.convertDateFormat()}"
                var all_items_percentage=0.0
                /* listConstructionStageCondition.forEach {
                     all_items_percentage+=it.percentage!!
                     println("all_items_percentage:: "+all_items_percentage+"  percentage:: "+it.percentage!!)
                 }
                 var actpercentage = ((all_items_percentage)/listConstructionStageCondition.size).toDouble()
                  percentage = actpercentage.toBigDecimal().setScale(1, RoundingMode.UP).toInt()*/

                var progresstext=""
                percentage= AppController.progres
                if (percentage == 100){
                    progresstext= binding.root.context.getString(R.string.complete)
                }else {
                    progresstext=binding.root.context.getString(R.string.progress_percentage, "${percentage}%")
                    if(percentage == 0){
                        progresstext=  "Pending"

                    }

                }
                binding.tvProgress.text =progresstext
                binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar)
                binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.appColor))
                binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.appColor))
                binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home))
                binding.tvDate.visibility = View.INVISIBLE

            } else {
                percentage = progressData.percentage!!
                var progresstext=""

                if (percentage == 100) {
//                binding.tvDate.text = "Completed ${progressData.date?.convertDateFormat()}"
//                 binding.tvDate.text = "${completedStageCount?.toInt()} Tasks to Complete"
//                 progresstext= binding.root.context.getString(R.string.completed)

                    var dateList=sortBydateList(progressData.userJobProgressItem)
                    progresstext= "Completed ${dateList[0].dateactual?.convertDateFormat()}"
                    binding.tvProgress.text = "Complete"

                    binding.tvDate.text = progresstext
                    binding.tvDate.visibility=View.VISIBLE

                } else {
                    if (percentage == 0) {
                        if( progressData.noOfTasks!=null && progressData.noOfCompletedTasks?.toInt()==0) {
                            binding.tvDate.text =
                                "${progressData.noOfTasks} Tasks to Complete"
                        }
                        binding.tvProgress.text = "Pending"
                    } else {
                        progresstext =
                            "${progressData.noOfCompletedTasks?.toInt()} of ${progressData.noOfTasks} Tasks Complete"
//                     binding.tvDate.text = "${completedStageCount?.toInt()} Tasks to Complete"
//                     progresstext=binding.root.context.getString(R.string.progress_percentage, "${progressData.noOfTasks}%")

                        binding.tvProgress.text = binding.root.context.getString(
                            R.string.progress_percentage,
                            "${percentage}%"
                        )
                        binding.tvDate.text = progresstext

                    }
                }

                binding.tvDate.visibility = View.VISIBLE
            }
            binding.root.setOnClickListener{
                if (progressData.stageName!="Your New Home") {
                    var prgressData = ProgressData(
                        progressData.stageName!!,
                        binding.tvProgress.text.toString(),
                        percentage,
                        "",
                        completedStageCount!!.toInt(),
                        totalStages!!.toInt(),
                        ProgressStage.AdminStage,
                        progressData.userJobProgressItem
                    )
                    callBack.invoke(prgressData)
                }


            }
            when (adapterPosition) {
                1-> {
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_admin)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_admin_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_admin))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_admin_stage))
                    binding.tvDescription.text = context.getString(R.string.home_admin_small_desc)+"\n  "
                }
                2 ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_base)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_base_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_base_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_base_stage))
                    binding.tvDescription.text = context.getString(R.string.home_base_small_desc)+"\n  "
                }
                3 ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_frame)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_frame_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_frame_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_frame_stage))
                    binding.tvDescription.text = context.getString(R.string.home_frame_small_desc)+"\n  "
                }
                4 ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_lockup)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_lockup_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_lockup_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_lockup_stage))
                    binding.tvDescription.text = context.getString(R.string.home_lockup_small_desc)+"\n  "
                }
                5 ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_fixing)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_fixing_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_fixing_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_fixing_stage))
                    binding.tvDescription.text = context.getString(R.string.home_fixing_small_desc)+"\n  "
                }
                6 ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_finishing)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_finishing_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_finishing_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_final_stage))
                    binding.tvDescription.text = context.getString(R.string.home_finishing_small_desc)+"\n  "
                }
                7->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_base)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_base_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_base_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_base_stage))
                    binding.tvDescription.text = context.getString(R.string.home_base_small_desc)+"\n  "
                }
                8->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar_fixing)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.color_fixing_stage))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.color_fixing_stage))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_fixing_stage))
                    binding.tvDescription.text = context.getString(R.string.home_fixing_small_desc)+"\n  "
                }
                else ->{
                    binding.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.circular_progress_bar)
                    binding.tvSeeMore.setTextColor(ContextCompat.getColor(context, R.color.appColor))
                    binding.tvProgress.setTextColor(ContextCompat.getColor(context, R.color.appColor))
                    binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home))
//                    binding.tvDate.visibility = View.INVISIBLE
                    binding.tvDescription.text = context.getString(R.string.home_new_home_small_desc)
                }

            }
            binding.progress.progress = percentage!!


        }
    }
    private fun sortBydateList(documentsList : List<UserJobProgressItem>):List<UserJobProgressItem>{
        val sortedList = documentsList.sortedByDescending {  it.dateactual }
        return sortedList
    }
}