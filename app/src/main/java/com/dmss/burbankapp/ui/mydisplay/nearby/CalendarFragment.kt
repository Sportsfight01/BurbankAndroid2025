package com.dmss.burbankapp.ui.mydisplay.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.databinding.FragmentCalendarBinding
import com.dmss.burbankapp.ui.mydisplay.DisplayToolbarViewModel
import com.dmss.burbankapp.ui.mydisplay.MyDisplayHomeFragment
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment() {

    lateinit var binding: FragmentCalendarBinding
    var cusrrentDate: String? = null

    var estateName: String? = null
    var street: String? = null
    var suburb: String? = null
    var houseName: String? = null
    var houseSize: String? = null
    lateinit var mPreferences: CustomSharedPreferences
    private lateinit var displayToolbarViewModel: DisplayToolbarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConstants.BACKSTACK_COUNT = 0
        binding.llBack.setOnClickListener {
            activity?.onBackPressed()
        }
        // todo change this line
       // (parentFragment as MyDisplayHomeFragment).toolBinding.tvTool.text = "Book an Appointment"
        mPreferences = CustomSharedPreferences(requireContext())

        Timber.e("DisplayId is ---> ${mPreferences.getDisplayId()}")
        displayToolbarViewModel = ViewModelProviders.of(this)[DisplayToolbarViewModel::class.java]
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
            binding.tvStreetSuburb.text = ("$street,\n$suburb")
        }
        //Disable previous dates
        binding.calendarView.minDate = System.currentTimeMillis() - 1000

        binding.calendarView.firstDayOfWeek = 2

        binding.btnSelectTime.setOnClickListener {
            navigateToSelectTime()
        }
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = ("${month + 1}/$dayOfMonth/$year")
            val parseFormat = SimpleDateFormat("EEEE, MMMM dd yyyy")
            val format = SimpleDateFormat("MM/dd/yyyy")
            val date: Date = format.parse(selectedDate)
            cusrrentDate = parseFormat.format(date)
            scrollNestedScrollViewDown()
        }
    }

    private fun scrollNestedScrollViewDown() {
        binding.nsCalendar.postDelayed(Runnable {
            binding.nsCalendar.fullScroll(View.FOCUS_DOWN)
        }, 200)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    private fun navigateToSelectTime() {
        if (cusrrentDate == null) {
            val parseFormat = SimpleDateFormat("EEEE, MMMM dd yyyy")
            cusrrentDate = parseFormat.format(binding.calendarView.date)
        }

        Timber.e("Selected Date is : $cusrrentDate")

        if (estateName != null && street != null && suburb != null && houseName != null) {
            var bundle = Bundle()
            bundle.putString("estateName", estateName)
            bundle.putString("street", street)
            bundle.putString("suburb", suburb)
            bundle.putString("houseName", houseName)
            bundle.putString("selectedDate", cusrrentDate)
            bundle.putString("houseSize", houseSize)
            loadFragment(ChooseYourTimeFragment(), bundle)
        }

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