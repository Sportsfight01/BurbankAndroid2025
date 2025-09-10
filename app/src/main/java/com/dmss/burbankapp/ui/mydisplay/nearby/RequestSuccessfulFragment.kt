package com.dmss.burbankapp.ui.mydisplay.nearby

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dmss.burbankapp.R
import com.dmss.burbankapp.databinding.FragmentRequestSuccessfulBinding
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.greenrobot.eventbus.EventBus


class RequestSuccessfulFragment : Fragment() {
    private lateinit var binding: FragmentRequestSuccessfulBinding
    var estateName: String? = null
    var street: String? = null
    var suburb: String? = null
    var houseName: String? = null
    var day: String? = null
    var selectedDate: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestSuccessfulBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
        showSuccessDialog()
        AppConstants.BACKSTACK_COUNT = 0
        if (arguments != null) {
            estateName = arguments?.getString("estateName")
            street = arguments?.getString("street")
            suburb = arguments?.getString("suburb")
            houseName = arguments?.getString("houseName")
            day = arguments?.getString("day")
            selectedDate = arguments?.getString("selectedDate")
        }
        if (estateName != null && day != null && selectedDate != null) {
            binding.tvHeader.text = estateName
            binding.tvDay.text = selectedDate
            binding.tvMorning.text = day
        }

        binding.llBack.setOnClickListener {

            AppConstants.HOMEANDLAND_TAP = 1
            startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 2)
            )
            activity!!.finish()
          /*  val appEvent = AppEvent(
                AppEvent.DISPLAYHOMES_EVENT, ""
            )
            EventBus.getDefault().post(appEvent)
            requireActivity().onBackPressed()*/
        }
    }

    private fun showSuccessDialog() {

        AppUtils.showValidationAlert(requireContext(),getString(R.string.thank_you_for_your_appointment_request_a_new_home_consultant_will_be_in_touch_with_your_precise_appointment_time))
       /* val dialogBuilder = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.thank_you_for_your_appointment_request_a_new_home_consultant_will_be_in_touch_with_your_precise_appointment_time))
            .setPositiveButton("OK", null)
            .create()
            .show()*/
      /*  val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.app_name))
        dialogBuilder.setMessage(getString(R.string.thank_you_for_your_appointment_request_a_new_home_consultant_will_be_in_touch_with_your_precise_appointment_time))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { dialog, _ ->
                dialog.dismiss()
            }
          *//*  .setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>")) { dialog, _ ->
                dialog.cancel()
            }*//*

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        // show alert dialog
        alert.show()*/
    }

}