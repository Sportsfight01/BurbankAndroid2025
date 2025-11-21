package com.dmss.burbankapp.utility

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.dmss.burbankapp.R

class ViewPageDotIndicator {
    private lateinit var dots: Array<ImageView?>

    private var activity: Context? = null
    var dotsLayout: LinearLayout? = null

    constructor(context: Context, viewPager: ViewPager, dotsLayout: LinearLayout) {
        this.activity = context
        this.dotsLayout = dotsLayout

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })

    }


    fun setupDots(count: Int) {
        dots = arrayOfNulls(count)
        dotsLayout?.removeAllViews()

        for (i in 0 until count) {
            dots[i] = ImageView(activity).apply {
                setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.dot_inactive))
            }
            val params = LinearLayout.LayoutParams(20, 20)
            params.setMargins(8, 0, 8, 0)
            dotsLayout?.addView(dots[i], params)
        }
        updateDots(0)
    }

    private fun updateDots(position: Int) {
        for (i in dots.indices) {
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    activity!!,
                    if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
                )
            )
        }
    }
}