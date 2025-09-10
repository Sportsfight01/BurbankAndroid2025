package com.dmss.burbankappold.dashboard.ui.finanace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.databinding.FragmentFinanceDetailsBinding
import com.dmss.burbankappold.utils.*
import common.AppController
import kotlinx.coroutines.flow.combine
import models.finance.FinanceAmount
import models.finance.FinanceData

class FinanceDetailsFragment : Fragment() {
    private var _binding: FragmentFinanceDetailsBinding? = null
    private val binding get() = _binding!!
    private var financeData: FinanceData? =null
    private var type: Int? = null
    private var contractValue : Double = 0.0
    private var financeItemAdapter : FinanceItemsAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFinanceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            financeData = it.get(BundleKey.FINANCE_DATA) as FinanceData
            type = it.getInt(BundleKey.FINANCE_TYPE)
            contractValue = it.getDouble(BundleKey.CONTRACT_VALUE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardNewActivity).initToolBarWithBackBackButton()
        val lManager = LinearLayoutManager(context)
        binding.tvDate.text = "Last Updated ${AppController.lastUpdatedDate.convertDateFormatFincDetaild(PHOTOS_DATE_FORMAT, ACTUAL_DATE_FORMAT)}"
        when(type){
            0,1,2,3 ->{
                binding.tvContractValue.text = contractValue.toString().convertUsCurrency()
                binding.tvBalanceDue.text = "0.0".convertUsCurrency()
                financeData?.financeVariations?.let {
                    financeItemAdapter = FinanceItemsAdapter(it)
                    binding.rlVariations.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = financeItemAdapter
                    }
                }
                financeData?.financeClaims?.let {
                    financeItemAdapter = FinanceItemsAdapter(it)
                    binding.rlClaims.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = financeItemAdapter
                    }
                }
                financeData?.financeReceipts?.let {
                    financeItemAdapter = FinanceItemsAdapter(it)
                    binding.rlReceipts.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = financeItemAdapter
                    }
                }

            }
           /* 1 ->{
              *//*  binding.llContractDetails.hide()
                binding.llClaims.hide()
                binding.llReceipts.hide()*//*
                financeData?.FinanceVariations?.let {
                    financeItemAdapter = FinanceItemsAdapter(it)
                    binding.rlVariations.apply {
                        layoutManager =lManager
                        adapter = financeItemAdapter
                    }
                }

            }
            2 ->{
              *//*  binding.llContractDetails.hide()
                binding.llVariations.hide()
                binding.llReceipts.hide()*//*
                financeData?.FinanceClaims?.let {
                    financeItemAdapter = FinanceItemsAdapter(it)
                    binding.rlClaims.apply {
                        layoutManager =lManager
                        adapter = financeItemAdapter
                    }
                }
            }
            3 ->{
               *//* binding.llContractDetails.hide()
                binding.llClaims.hide()
                binding.llVariations.hide()*//*
                financeData?.FinanceReceipts?.let {
                    financeItemAdapter = FinanceItemsAdapter(it)
                    binding.rlReceipts.apply {
                        layoutManager =lManager
                        adapter = financeItemAdapter
                    }
                }
            }*/
        }
        binding.tvApprovedValue.text = FinanceFragment.getApprovedVariation(financeData)
        binding.tvAdjustValue.text = FinanceFragment.getContactValue(financeData)
        binding.tvTotalAmountClaimed.text = FinanceFragment.getTotalAmountClaimed(financeData)
        binding.tvTotalAmountReceived.text = FinanceFragment.getTotalAmountReceived(financeData)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}