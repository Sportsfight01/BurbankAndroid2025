package com.dmss.burbankappold.dashboard.ui.home

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.ui.home.adapters.ProgressTasksAdapter
import com.dmss.burbankappold.databinding.FragmentProgressDetailsBinding
import com.dmss.burbankappold.utils.AppConstants
import com.dmss.burbankappold.utils.BundleKey
import com.dmss.burbankappold.utils.SharedViewModel
import models.photos.PhotosDataItem
import models.progress.ProgressData
import models.progress.ProgressDetailsTasks

class ProgressDetailsFragment : BaseFragment() {
    private var _binding : FragmentProgressDetailsBinding? = null
    private val binding get() = _binding!!
    private var mProgressData : ProgressData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProgressDetailsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressData = arguments?.let {
            it.get(BundleKey.PROGRESS_DATA) as ProgressData
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        (activity as DashboardNewActivity).initToolBarWithBackBackButton()
        var viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.selectedItem("this is new item")
    }
    fun animateTextView(initialValue: Int, finalValue: Int, textview: TextView) {
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
        valueAnimator.duration = 1500
        valueAnimator.addUpdateListener { valueAnimator ->
            textview.text = valueAnimator.animatedValue.toString() + "%"
        }
        valueAnimator.start()
    }
    private fun initViews() {


        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            println("adapter Size:: "+getTaskData().size)
            adapter = ProgressTasksAdapter(sortBydateList(getTaskData()))
        }

//        binding.tvDesc.text = mProgressData?.desc
        binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_admin_stage))

        binding.tvTasks.text = "${mProgressData?.tasksCompleted} of ${mProgressData?.totalTasks} Tasks Complete"
        binding.progress.progress = mProgressData!!.progress

        mProgressData?.let {
            binding.tvStage.text = it.name
            binding.imgLogo.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), getStageIcon(it.name))
            )
            binding.tvProgress.text = if (it.progress > 0) "${it.progress}%" else "0%"
            if(it.progress>0) {
                ObjectAnimator.ofInt(binding.progress,"progress",it.progress).setDuration(2000).start()
                animateTextView(0, it.progress, binding.tvProgress)
            }else{
                binding.tvProgress.text = "0%"
                binding.progress.progress = it.progress

            }

        println("Stage Name Details :: "+it.name)
        when(it.name){
            AppConstants.Admin_Stage -> {
                binding.tvDesc.text = context?.getString(R.string.home_admin_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_admin_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_admin_stage)
                )
            }
            AppConstants.Base_Stage -> {
                binding.tvDesc.text = context?.getString(R.string.home_base_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_base_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_base_stage)
                )
            }
            AppConstants.Frame_Stage -> {
                binding.tvDesc.text = context?.getString(R.string.home_frame_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_frame_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_frame_stage)
                )
            }
            AppConstants.Lockup_Stage -> {
                binding.tvDesc.text = context?.getString(R.string.home_lockup_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_lockup_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_lockup_stage)
                )
            }
            AppConstants.Fixing_Stage -> {
                binding.tvDesc.text = context?.getString(R.string.home_fixing_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_fixing_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_fixing_stage)
                )
            }
            AppConstants.Finishing_Stage -> {
                binding.tvDesc.text = context?.getString(R.string.home_finishing_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_finishing_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_finishing_stage)
                )
            }
            else ->{}
        }
        }
    }

    private fun getTaskData(): List<ProgressDetailsTasks>{
        val list = arrayListOf<ProgressDetailsTasks>()
        val incompleteList = arrayListOf<ProgressDetailsTasks>()
        mProgressData?.list?.forEach{
            var date=""
            if( it.dateactual!=null) {
                date=it.dateactual!!
            }
            if(it.status=="Complete") {
                list.add(
                    ProgressDetailsTasks(
                        it.name!!,
                        date,
                        it.status!!,
                        mProgressData?.progressStage,
                        it.stageName
                    )
                )
            }else{
                incompleteList.add(
                    ProgressDetailsTasks(
                        it.name!!,
                        date,
                        it.status!!,
                        mProgressData?.progressStage,
                        it.stageName
                    )
                )
            }

        }
        println("mProgressData?.list::$list")

        var sortBydateList=list
        sortBydateList.addAll(incompleteList)

        return sortBydateList
    }
    private fun sortBydateList(documentsList : List<ProgressDetailsTasks>): List<ProgressDetailsTasks> {
        val sortedList = documentsList.sortedByDescending {  it.date }
        return sortedList
    }
    companion object{
       /* fun getStageColor(progressStage: ProgressStage?): Int{
            println("ProgressStage::getStageColor0 "+progressStage)
            return when(progressStage){
                ProgressStage.AdminStage -> R.color.color_admin_stage
                ProgressStage.Administration -> R.color.color_admin_stage

                ProgressStage.BaseStage -> R.color.color_base_stage
                ProgressStage.FrameStage -> R.color.color_frame_stage
                ProgressStage.LockupStage -> R.color.color_lockup_stage
                ProgressStage.FixingStage -> R.color.color_fixing_stage
                ProgressStage.FinishingStage -> R.color.color_finishing_stage
                else -> R.color.color_base_stage
            }*/
            fun getStageColor(progressStage: String): Int{
//                println("ProgressStage::getStageColor0 "+progressStage)
                return when(progressStage){
                    AppConstants.Admin_Stage -> R.color.color_admin_stage
                    "Administration" -> R.color.color_admin_stage
                    AppConstants.Base_Stage -> R.color.color_base_stage
                    AppConstants.Frame_Stage -> R.color.color_frame_stage
                    AppConstants.Lockup_Stage -> R.color.color_lockup_stage
                    AppConstants.Fixing_Stage -> R.color.color_fixing_stage
                    AppConstants.Finishing_Stage -> R.color.color_finishing_stage
                    else -> R.color.color_admin_stage
                }
        }
    }

    private fun getStageIcon(progressStage: String?): Int{
        return when(progressStage){
            AppConstants.Admin_Stage -> R.drawable.ic_admin
            AppConstants.Base_Stage -> R.drawable.ic_home_base_stage
            AppConstants.Frame_Stage -> R.drawable.ic_home_frame_stage
            AppConstants.Lockup_Stage -> R.drawable.ic_home_lockup_stage
            AppConstants.Fixing_Stage -> R.drawable.ic_home_fixing_stage
            AppConstants.Finishing_Stage -> R.drawable.ic_home_final_stage
            else -> R.drawable.ic_home
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/*
class ProgressDetailsFragment : BaseFragment() {
    private var _binding : FragmentProgressDetailsBinding? = null
    private val binding get() = _binding!!
    private var mProgressData : ProgressData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProgressDetailsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressData = arguments?.let {
            it.get(BundleKey.PROGRESS_DATA) as ProgressData
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        (activity as DashboardNewActivity).initToolBarWithBackBackButton()
        var viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.selectedItem("this is new item")
    }
    fun animateTextView(initialValue: Int, finalValue: Int, textview: TextView) {
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
        valueAnimator.duration = 1500
        valueAnimator.addUpdateListener { valueAnimator ->
            textview.text = valueAnimator.animatedValue.toString() + "%"
        }
        valueAnimator.start()
    }
    private fun initViews() {
        binding.tvTasks.text = "${mProgressData?.tasksCompleted} of ${mProgressData?.totalTasks} Tasks Complete"
        mProgressData?.let {
            binding.tvStage.text = it.progressStage.value
//            binding.tvProgress.text = if (it.progress > 0) "${it.progress}%" else "0%"
            if(it.progress>0) {
                ObjectAnimator.ofInt(binding.progress,"progress",it.progress).setDuration(2000).start()
                animateTextView(0, it.progress, binding.tvProgress)
            }else{
                binding.tvProgress.text = "0%"
                binding.progress.progress = it.progress

            }
        }

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ProgressTasksAdapter(getTaskData())
        }
        binding.imgLogo.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), getStageIcon(mProgressData?.progressStage))
        )

        when(mProgressData?.progressStage){
            ProgressStage.AdminStage -> {
                binding.tvDesc.text = context?.getString(R.string.home_admin_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_admin_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_admin_stage)
                )
            }
            ProgressStage.BaseStage -> {
                binding.tvDesc.text = context?.getString(R.string.home_base_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_base_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_base_stage)
                )
            }
            ProgressStage.FrameStage -> {
                binding.tvDesc.text = context?.getString(R.string.home_frame_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_frame_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_frame_stage)
                )
            }
            ProgressStage.LockupStage -> {
                binding.tvDesc.text = context?.getString(R.string.home_lockup_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_lockup_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_lockup_stage)
                )
            }
            ProgressStage.FixingStage -> {
                binding.tvDesc.text = context?.getString(R.string.home_fixing_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_fixing_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_fixing_stage)
                )
            }
            ProgressStage.FinishingStage -> {
                binding.tvDesc.text = context?.getString(R.string.home_finishing_large_desc)
                binding.progress.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.color_finishing_stage))
                binding.imgStage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.home_img_finishing_stage)
                )
            }
            else ->{}
        }
    }

    private fun getTaskData(): List<ProgressDetailsTasks>{
        val list = arrayListOf<ProgressDetailsTasks>()
        val incompleteList = arrayListOf<ProgressDetailsTasks>()

        mProgressData?.list?.forEach{
            var date=""
            if( it.dateactual!=null) {
                date=it.dateactual!!
            }
            if(it.status=="Completed") {
                list.add(
                    ProgressDetailsTasks(
                        it.name!!,
                        date,
                        it.status!!,
                        mProgressData?.progressStage
                    )
                )
            }else{
                incompleteList.add(
                    ProgressDetailsTasks(
                        it.name!!,
                        date,
                        it.status!!,
                        mProgressData?.progressStage
                    )
                )
            }

        }
        var sortBydateList=sortBydateList(list).toMutableList()
        sortBydateList.addAll(incompleteList)

        return sortBydateList
    }
    private fun sortBydateList(documentsList : List<ProgressDetailsTasks>): List<ProgressDetailsTasks> {
        val sortedList = documentsList.sortedByDescending {  it.date }
        return sortedList
    }
    companion object{
        fun getStageColor(progressStage: ProgressStage?): Int{
            return when(progressStage){
                ProgressStage.AdminStage -> R.color.color_admin_stage
                ProgressStage.BaseStage -> R.color.color_base_stage
                ProgressStage.FrameStage -> R.color.color_frame_stage
                ProgressStage.LockupStage -> R.color.color_lockup_stage
                ProgressStage.FixingStage -> R.color.color_fixing_stage
                ProgressStage.FinishingStage -> R.color.color_finishing_stage
                else -> R.color.color_base_stage
            }
        }
    }

    private fun getStageIcon(progressStage: ProgressStage?): Int{
        return when(progressStage){
            ProgressStage.AdminStage -> R.drawable.ic_admin
            ProgressStage.BaseStage -> R.drawable.ic_home_base_stage
            ProgressStage.FrameStage -> R.drawable.ic_home_frame_stage
            ProgressStage.LockupStage -> R.drawable.ic_home_lockup_stage
            ProgressStage.FixingStage -> R.drawable.ic_home_fixing_stage
            ProgressStage.FinishingStage -> R.drawable.ic_home_final_stage
            else -> R.drawable.ic_home
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/
