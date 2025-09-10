package com.dmss.burbankappold.dashboard.ui.finanace

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.ItemFinanceLayoutBinding
import com.dmss.burbankappold.databinding.ItemFinanceOverviewLayoutBinding
import com.dmss.burbankappold.utils.convertUsCurrency
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import models.finance.FinanceAmount
import models.finance.FinanceData
import models.finance.FinanceHomeData

class FinanceAdapter(val callBack: (Int, FinanceData?) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var list = listOf<FinanceHomeData>()
    fun setFinanceList(list: List<FinanceHomeData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = ItemFinanceOverviewLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            OverviewViewHolder(binding)
        } else {
            val binding = ItemFinanceLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) (holder as OverviewViewHolder).bind(list[position])
        else (holder as ViewHolder).bind(list[position])
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    inner class ViewHolder(val binding: ItemFinanceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(financeHomeData: FinanceHomeData) {
            binding.tvName.text = financeHomeData.title
            val context = binding.root.context
            val financeAmountList: ArrayList<FinanceAmount>
            when (financeHomeData.type) {
                1 -> {
                    financeAmountList = getFinanceData(financeHomeData.financeData?.financeVariations)
                    binding.clVariationLayout.show()
                    binding.tvContractAmount.text = context.getString(R.string.add_minus_apporved)
                    binding.tvContractValue.text = FinanceFragment.getApprovedVariation(financeHomeData.financeData)
                    binding.tvTotalAmount.text = context.getString(R.string.adjusted_contract_value)
                    binding.tvTotalAmountValue.text = FinanceFragment.getContactValue(financeHomeData.financeData)
                }
                2 -> {
                    financeAmountList = getFinanceData(financeHomeData.financeData?.financeClaims)
                    binding.clVariationLayout.hide()
                    binding.tvTotalAmount.text = context.getString(R.string.total_amount_claimed)
                    binding.tvTotalAmountValue.text = FinanceFragment.getTotalAmountClaimed(financeHomeData.financeData)
                }
                else ->{
                    financeAmountList = getFinanceData(financeHomeData.financeData?.financeReceipts)
                    binding.clVariationLayout.hide()
                    binding.tvTotalAmount.text = context.getString(R.string.total_amount_received)
                    binding.tvTotalAmountValue.text = FinanceFragment.getTotalAmountReceived(financeHomeData.financeData)
                }
            }
            binding.rvFinanceInfo.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = FinanceItemsAdapter(financeAmountList)
            }

            binding.root.setOnClickListener {
                callBack.invoke(adapterPosition, financeHomeData.financeData)
            }
        }
        private fun getFinanceData(financeVarList: List<FinanceAmount>?): ArrayList<FinanceAmount> {
            val list = arrayListOf<FinanceAmount>()
            if (financeVarList != null && financeVarList.size > 3) {
                for (i in 0..3) {
                    list.add(financeVarList[i])
                }
                binding.tvSeeMore.show()
            } else {
                financeVarList?.forEach {
                    list.add(it)
                }
                // modified by Durga 15/11/2022
//                binding.tvSeeMore.hide()
                binding.tvSeeMore.show()

            }
            return list
        }
    }

    inner class OverviewViewHolder(val binding: ItemFinanceOverviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(financeHomeData: FinanceHomeData) {
            binding.tvApprovedValue.text = FinanceFragment.getApprovedVariation(financeHomeData.financeData)
            binding.tvAdjustedValue.text = FinanceFragment.getContactValue(financeHomeData.financeData)
            binding.tvTotalClaimed.text = FinanceFragment.getTotalAmountClaimed(financeHomeData.financeData)
            binding.tvTotalReceived.text = FinanceFragment.getTotalAmountReceived(financeHomeData.financeData)
            binding.root.setOnClickListener {
                callBack.invoke(0, list[0].financeData)
            }
        }
    }
}