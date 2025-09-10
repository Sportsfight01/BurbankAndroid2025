package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays

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
import com.dmss.burbankapp.databinding.FragmentFavoriteChooseYourTimeBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.ui.mydisplay.nearby.ChooseYourTimeCommentFragment
import com.dmss.burbankapp.utils.AppConstants


class FavoriteChooseYourTimeFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteChooseYourTimeBinding
    lateinit var toolBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

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
        // Inflate the layout for this fragment
        binding = FragmentFavoriteChooseYourTimeBinding.inflate(inflater, container, false)
        toolBinding = HomelandToolBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

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
        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        profileWithBadgeBinding.tvFavorites.visibility = View.VISIBLE
        toolBinding.tvTool.visibility= View.VISIBLE
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        toolBinding.tvTool.text=getString(R.string.book_an_appointment_small)
        binding.llBack.setOnClickListener {
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
            bundle.putString("day", binding.tvMorning.text.toString())
//            loadFragment(FavoriteChooseYourTimeCommentFragment(), bundle)
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
            bundle.putString("day", binding.tvMiddle.text.toString())
//            loadFragment(FavoriteChooseYourTimeCommentFragment(), bundle)
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
            bundle.putString("houseSize", houseSize)
            bundle.putString("day", binding.tvAfternoon.text.toString())
//            loadFragment(FavoriteChooseYourTimeCommentFragment(), bundle)
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
        val transaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        if (fragment.isAdded) {
            return
        }

        transaction.add(R.id.fl_content, fragment)
        fragment.arguments = bundle
        transaction.addToBackStack(null)
        transaction.commit()
    }


}