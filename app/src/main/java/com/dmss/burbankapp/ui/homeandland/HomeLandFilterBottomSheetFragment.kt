package com.dmss.burbankapp.ui.homeandland

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.databinding.FragmentHomeLandFilterBottomSheetBinding
import com.dmss.burbankapp.ui.view.ISortingItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HomeLandFilterBottomSheetFragment(var listener: ISortingItem) : BottomSheetDialogFragment() {
    val TAG = "HomeLandFilterBottomSheetFragment"
    lateinit var itemView: View

    private lateinit var mListener: ISortingItem

    lateinit var customSharedPreferences: CustomSharedPreferences

    lateinit var binding: FragmentHomeLandFilterBottomSheetBinding
    fun newInstance(listener: ISortingItem): HomeLandFilterBottomSheetFragment? {
        return HomeLandFilterBottomSheetFragment(listener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeLandFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.mListener = listener
        initView()


    }

    private fun initView() {
        customSharedPreferences = CustomSharedPreferences(requireContext())

        if (customSharedPreferences.getPriceRange() == "High") {
            binding.tvLow.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_text_font_3_1
                )
            )
            binding.tvHigh.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            binding.tvHigh.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_text_font_3_1
                )
            )
            binding.tvLow.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        binding.rlDone.setOnClickListener {
            dialog?.dismiss()
        }
        binding.tvHigh.setOnClickListener {

            binding.tvLow.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_text_font_3_1
                )
            )
            binding.tvHigh.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            mListener.sortItem("1")
            customSharedPreferences.savePriceRange("High")
        }
        binding.tvLow.setOnClickListener {
            binding.tvHigh.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_text_font_3_1
                )
            )
            binding.tvLow.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            mListener.sortItem("0")
            customSharedPreferences.savePriceRange("Low")

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


}
