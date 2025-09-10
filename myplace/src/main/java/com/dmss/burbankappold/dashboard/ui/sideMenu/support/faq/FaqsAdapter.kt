package com.dmss.burbankappold.dashboard.ui.sideMenu.support.faq

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.*
import com.dmss.burbankappold.utils.*
import models.faq.FAQDataItem
import models.faq.FilterFaqList
interface CellClickListener {
    fun onCellClickListener(position: Int)
}
class FaqsAdapter(private val cellClickListener: CellClickListener) : RecyclerView.Adapter<FaqsAdapter.ViewHolder>() {
    private var lastSelectedPosition = -1
    private var mFaqList = arrayListOf<FilterFaqList>()
    fun setFaqList(list: ArrayList<FilterFaqList>){
        mFaqList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFaqHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mFaqList[position].let { holder.bind(it) }

    }

    override fun getItemCount(): Int {
        return mFaqList.size
    }

    inner class ViewHolder(val binding: ItemFaqHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FilterFaqList) {
            val mContext = binding.root.context
            binding.lblListHeader.text = item.heading
            binding.faqSubList.apply {
                this.layoutManager = LinearLayoutManager(binding.root.context)
                this.adapter = FaqsChildAdapter(item.subList)
            }
            if (item.isExpanded){
                binding.faqSubList.show()
                binding.ivDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_faq_right_arrow
                    )
                )
                binding.lblListHeader.setTextColor(ContextCompat.getColor(mContext, R.color.appColor))
                binding.ivDropDown.rotation = 90f
            }else{
                binding.faqSubList.hide()
                binding.ivDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_faq_right_arrow
                    )
                )
                binding.lblListHeader.setTextColor(ContextCompat.getColor(mContext, R.color.black))
                binding.ivDropDown.rotation = 0f
            }

            binding.root.setOnClickListener {
                cellClickListener.onCellClickListener(adapterPosition)

                if (lastSelectedPosition != -1 && lastSelectedPosition != adapterPosition) {
                    mFaqList[lastSelectedPosition].apply {
                        this.isExpanded = false
                    }
                    notifyItemChanged(lastSelectedPosition)
                }
                item.apply {
                    this.isExpanded = !item.isExpanded
                }
                notifyItemChanged(adapterPosition)
                lastSelectedPosition = adapterPosition
            }
        }
    }
}