package com.dmss.burbankappold.homecare

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dmss.burbankappold.R
import com.dmss.burbankappold.homecare.reports.NewIssueFragment
import com.dmss.burbankappold.homecare.reports.SelectYourIssueTypeFragmetn

open class HomeCareBaseFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        when (parentFragmentManager.primaryNavigationFragment) {
             is NewIssueFragment->{
//                changeStatusBarColor(R.color.white)
                (activity as HomeCareDashboardActivity).changeProfileBg(R.color.white)
            }
          /*  is SelectYourIssueTypeFragmetn ->{
//                (activity as DashboardNewActivity).initToolBarWithBackBackButton(isTrasToolBar = true)
            }*/
            else ->{
                (activity as HomeCareDashboardActivity).changeProfileBg(R.color.new_gray_color)

//                (activity as HomeCareDashboardActivity).updateToolBarTitle()
//                changeStatusBarColor(R.color.new_gray_color)
            }
        }
    }

    open fun changeStatusBarColor() {
        changeStatusBarColor(Color.WHITE)
    }

    open fun changeStatusBarColor(color: Int) {
        val window: Window = requireActivity().window
        val statusBarColor = ContextCompat.getColor(requireContext(), color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (color == R.color.white) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else window.decorView.systemUiVisibility = 0
            window.statusBarColor = statusBarColor
        }
    }
}