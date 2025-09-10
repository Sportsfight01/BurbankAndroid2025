package com.dmss.burbankapp.ui.mydisplay.nearby

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.dmss.burbankapp.R
import com.dmss.burbankapp.databinding.FragmentChooseYourTimeBinding
import com.dmss.burbankapp.utils.AppConstants


class ChooseYourTimeFragment : Fragment() {
    lateinit var binding: FragmentChooseYourTimeBinding
    var estateName: String? = null
    var street: String? = null
    var suburb: String? = null
    var houseName: String? = null
    var houseSize: String? = null

    var selectedDate: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseYourTimeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        
    }

    private fun initView() {
        binding.llBack.setOnClickListener{
            requireActivity().onBackPressed()
        }
        AppConstants.BACKSTACK_COUNT = 0
        selectedDate = arguments?.getString("selectedDate")

        if (arguments != null) {
            estateName = arguments?.getString("estateName")
            street = arguments?.getString("street")
            suburb = arguments?.getString("suburb")
            houseName = arguments?.getString("houseName")
            houseSize = arguments?.getString("houseSize")
        }
        if (estateName != null && street != null && suburb != null && houseName != null) {
            binding.tvHeader.text = estateName
            binding.tvOndisplay.text = ("ON DISPLAY: $houseName")
            binding.tvStreetSuburb.text = ("$street,\n$suburb")
        }

        if (selectedDate != null) {


            binding.tvDay.text = selectedDate
        }


        binding.tvMorning.setOnClickListener {
            changeViewBackground(binding.tvMorning)
            var bundle = Bundle()
            if (selectedDate != null) {
                bundle.putString("selectedDate", selectedDate)
            }
            bundle.putString("estateName", estateName)
            bundle.putString("street", street)
            bundle.putString("suburb", suburb)
            bundle.putString("houseName", houseName)
            bundle.putString("day", "morning")
            bundle.putString("houseSize", houseSize)
//            loadFragment(ChooseYourTimeCommentFragment(), bundle)
            val intent: Intent = Intent(requireContext(), ChooseYourTimeCommentFragment::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.tvMiddle.setOnClickListener {
            changeViewBackground(binding.tvMiddle)
            var bundle = Bundle()
            if (selectedDate != null) {
                bundle.putString("selectedDate", selectedDate)
            }
            bundle.putString("estateName", estateName)
            bundle.putString("street", street)
            bundle.putString("suburb", suburb)
            bundle.putString("houseName", houseName)
            bundle.putString("day", "middle of the day")
//            loadFragment(ChooseYourTimeCommentFragment(), bundle)
            val intent: Intent = Intent(requireContext(), ChooseYourTimeCommentFragment::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.tvAfternoon.setOnClickListener {
            changeViewBackground(binding.tvAfternoon)
            var bundle = Bundle()
            if (selectedDate != null) {
                bundle.putString("selectedDate", selectedDate)
            }
            bundle.putString("estateName", estateName)
            bundle.putString("street", street)
            bundle.putString("suburb", suburb)
            bundle.putString("houseName", houseName)
            bundle.putString("day", "afternoon")
//            loadFragment(ChooseYourTimeCommentFragment(), bundle)
            val intent: Intent = Intent(requireContext(), ChooseYourTimeCommentFragment::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    fun changeViewBackground(view: TextView) {
        binding.tvMorning.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_grey_line)

        binding.tvAfternoon.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_grey_line)
        binding.tvMiddle.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_grey_line)

        binding.tvMorning.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.tvMiddle.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text_font_3_1))
        binding.tvAfternoon.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.grey_text_font_3_1
            )
        )
        view.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_orange_bg)
        view.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_3_1))
    }

    fun loadFragment(fragment: Fragment, bundle: Bundle) {
        // load fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragment.arguments = bundle
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }

}