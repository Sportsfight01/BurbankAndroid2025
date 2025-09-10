package com.dmss.burbankapp.ui.mydisplay.favoritesDisplays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.dmss.burbankapp.R
import com.dmss.burbankapp.databinding.FragmentFavoriteDisplayBookAppintmentBinding
import com.dmss.burbankapp.databinding.HomelandToolBinding
import com.dmss.burbankapp.databinding.ProfileWithBadgeBinding
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class FavoriteDisplayBookAppintmentFragment : Fragment() {
    var s: String? = null
    lateinit var toolBinding: HomelandToolBinding
    lateinit var profileWithBadgeBinding: ProfileWithBadgeBinding

    var estateName: String? = null
    var street: String? = null
    var suburb: String? = null
    var houseName: String? = null
    var houseSize: String? = null
    private lateinit var binding: FragmentFavoriteDisplayBookAppintmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteDisplayBookAppintmentBinding.inflate(inflater, container, false)
        toolBinding = HomelandToolBinding.bind(binding.root)
        profileWithBadgeBinding = ProfileWithBadgeBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
            toolBinding.tvTool.text = "See one of our display homes"

        }
        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
            toolBinding.tvTool.text = "See one of our display homes"

        }
        profileWithBadgeBinding.tvFavorites.visibility = View.VISIBLE
        toolBinding.tvTool.visibility= View.VISIBLE
        profileWithBadgeBinding.profileNotification.text=AppConstants.TotalMyFavs.toString()
        profileNotificationCountView(AppConstants.TotalMyFavs)
        AppConstants.BACKSTACK_COUNT = 0

        toolBinding.tvTool.text = getString(R.string.book_an_appointment_small)


        if (arguments != null) {
            estateName = arguments?.getString("estateName")
            street = arguments?.getString("street")
            suburb = arguments?.getString("suburb")
            houseName = arguments?.getString("houseName")
            houseSize = arguments?.getString("houseSize")
        }
        if (estateName != null && street != null && suburb != null && houseName != null) {
            binding.tvHeader.text = estateName
            if (houseName != null) {
                if (houseName!!.length > 1) {
                    houseName = AppUtils.removeLastIndexComma(houseName)
                }
            }
            binding.tvOndisplay.text = ("ON DISPLAY: $houseName")
            binding.tvStreetSuburb.text = ("$street, \n $suburb")
        }


        //Disable previous dates
        binding.calendarView.minDate = System.currentTimeMillis() - 1000

        binding.btnSelectTime.setOnClickListener {
            navigateToSelectTime()
        }


        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->


            var selectedDate = ("${month + 1}/$dayOfMonth/$year")
            val parseFormat = SimpleDateFormat("EEEE, MMMM dd yyyy")
            val format = SimpleDateFormat("MM/dd/yyyy")
            val date: Date = format.parse(selectedDate)
            s = parseFormat.format(date)

        }
    }

    private fun profileNotificationCountView(notificationCount:Int){
        if(notificationCount==0){
            profileWithBadgeBinding.profileNotification.visibility=View.GONE
        }else{
            profileWithBadgeBinding.profileNotification.visibility=View.VISIBLE
        }
    }

    private fun navigateToSelectTime() {
        if (s == null) {
            val parseFormat = SimpleDateFormat("EEEE, MMMM dd yyyy")
            s = parseFormat.format(binding.calendarView.date)
        }

        Timber.e("Selected Date is : $s")

        if (estateName != null && street != null && suburb != null && houseName != null) {
            var bundle = Bundle()
            bundle.putString("estateName", estateName)
            bundle.putString("street", street)
            bundle.putString("suburb", suburb)
            bundle.putString("houseName", houseName)
            bundle.putString("houseSize", houseSize)
            bundle.putString("selectedDate", s)
            loadFragment(FavoriteChooseYourTimeFragment(), bundle)
        }

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