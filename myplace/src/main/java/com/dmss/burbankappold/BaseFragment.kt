package com.dmss.burbankappold

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.ui.home.HomeFragment
import com.dmss.burbankappold.dashboard.ui.home.ProgressDetailsFragment

open class BaseFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        when(parentFragmentManager.primaryNavigationFragment){
            is HomeFragment->{
                changeStatusBarColor(R.color.white)
                (activity as DashboardNewActivity).initToolBar()
            }
            is ProgressDetailsFragment ->{
                changeStatusBarColor(R.color.new_gray_color)
                (activity as DashboardNewActivity).initToolBarWithBackBackButton(isTrasToolBar = true)
            }
            else ->{
                (activity as DashboardNewActivity).updateToolBarTitle()
                changeStatusBarColor(R.color.new_gray_color)
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
            if (color  == R.color.white) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }else window.decorView.systemUiVisibility = 0
            window.statusBarColor = statusBarColor
        }
    }

}