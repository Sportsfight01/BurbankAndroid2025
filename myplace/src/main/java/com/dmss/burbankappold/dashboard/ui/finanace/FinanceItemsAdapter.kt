package com.dmss.burbankappold.dashboard.ui.finanace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.ItemFinanceAmountLayoutBinding
import com.dmss.burbankappold.databinding.ItemProgressDetailsTaskBinding
import com.dmss.burbankappold.utils.convertDateFormat
import com.dmss.burbankappold.utils.convertUsCurrency
import models.finance.FinanceAmount
import models.progress.ProgressDetailsTasks

class FinanceItemsAdapter(val list: List<FinanceAmount>) : RecyclerView.Adapter<FinanceItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFinanceAmountLayoutBinding.inflate(
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

    inner class ViewHolder(val binding: ItemFinanceAmountLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(financeAmount: FinanceAmount) {
            binding.tvDesc.text = financeAmount.description
            binding.tvAmount.text = financeAmount.amount.toString().convertUsCurrency()

        }
    }
}