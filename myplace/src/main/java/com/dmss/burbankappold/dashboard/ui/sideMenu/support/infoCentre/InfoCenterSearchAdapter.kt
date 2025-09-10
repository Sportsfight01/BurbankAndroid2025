package com.dmss.burbankappold.dashboard.ui.sideMenu.support.infoCentre

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.ItemInfoSearchLayoutBinding
import models.infoCentre.InfoSearchData

class InfoCenterSearchAdapter(val callback: (String) -> Unit) : RecyclerView.Adapter<InfoCenterSearchAdapter.ViewHolder>() {
    var mInfoList = listOf<InfoSearchData>()
    private var lastCheckedRB: RadioButton? = null

    fun setInfoSearchData(list: List<InfoSearchData>){
        mInfoList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemInfoSearchLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mInfoList[position])
        /* holder.itemView.setOnClickListener{
             mInfoList[position].isChecked = !mInfoList[position].isChecked
             notifyItemChanged(position)
             callback.invoke(mInfoList[position].title)
         }*/
    }

    override fun getItemCount(): Int {
        return mInfoList.size
    }

    inner class ViewHolder(val binding: ItemInfoSearchLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoSearchData) {
            binding.tvCategory.text = item.title
//            binding.tvCategory.isChecked = item.isChecked


            binding.root.setOnClickListener { v ->
                var checkedRb: RadioButton = v as RadioButton;
                lastCheckedRB?.isChecked = false
                lastCheckedRB = checkedRb;
                mInfoList[position].isChecked = lastCheckedRB?.isChecked!!
                notifyItemChanged(position)
                callback.invoke(mInfoList[position].title)
            }
        }
    }
}