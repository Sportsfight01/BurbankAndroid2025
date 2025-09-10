package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dmss.burbankapp.R
import com.dmss.burbankapp.databinding.FragmentFavoriteRequesrSuccessfulBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.customviews.AppEvent
import org.greenrobot.eventbus.EventBus


class FavoriteRequestSuccessfulFragment : Fragment() {
    lateinit var toolBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    var estateName: String? = null
    var street: String? = null
    var suburb: String? = null
    var houseName: String? = null
    var day: String? = null
    var selectedDate: String? = null

    private lateinit var binding: FragmentFavoriteRequesrSuccessfulBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteRequesrSuccessfulBinding.inflate(inflater, container, false)
        toolBinding = HomelandToolBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {

        showSuccessDialog()

        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        profileWithBadgeBinding.tvFavorites.visibility = View.VISIBLE
        toolBinding.tvTool.visibility = View.VISIBLE
        toolBinding.tvTool.text = getString(R.string.book_an_appointment_small)

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
            val appEvent = AppEvent(
                AppEvent.DISPLAYHOMES_EVENT, ""
            )
            EventBus.getDefault().post(appEvent)
            requireActivity().onBackPressed()
        }
    }

    private fun showSuccessDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.thank_you_for_your_appointment_request_a_new_home_consultant_will_be_in_touch_with_your_precise_appointment_time))
            .setPositiveButton(Html.fromHtml("<font color='#f68521'>OK</font>")) {_, _ ->  }.create()
            .show()
    }

}