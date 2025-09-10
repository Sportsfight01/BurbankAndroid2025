package com.dmss.burbankapp.ui.designs

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar

import com.dmss.burbankapp.R
import com.dmss.burbankapp.utils.AppUtils
import kotlin.math.min

/**
 * A simple [Fragment] subclass.
 */
class PriceRangeMyCollectionFragment : Fragment() {
    lateinit var itemView:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        itemView = inflater.inflate(R.layout.fragment_price_range_my_collection, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val backIcon = itemView.findViewById<ImageView>(R.id.iv_back)
        val tvContinue = itemView.findViewById<TextView>(R.id.tv_continue)

        val tv_tool = itemView.findViewById<TextView>(R.id.tv_tool)
        val tv_how_does_it = itemView.findViewById<TextView>(R.id.tv_how_does_it)
        tv_tool.setText("< 10.5m,SINGLE")
        tv_how_does_it.visibility = View.INVISIBLE


        tvContinue.setOnClickListener {
            var fragment = MyCollectionFifthFragment();
            loadFragment(fragment)
        }
        val tv_designs = itemView.findViewById<TextView>(R.id.tv_designs)

        tv_designs.setOnClickListener {
            var fragment = MyCollectionPlacesFragment();
            loadFragment(fragment)
        }
        backIcon.setOnClickListener {
            activity!!.onBackPressed()
        }
        setRange();
    }

    fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        if (fragment.isAdded) {
            return
        }
        transaction.replace(R.id.fl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    @SuppressLint("SetTextI18n")
    private fun setRange() {

        val rangeSeekbar =
            itemView.findViewById(R.id.seekbar) as CrystalRangeSeekbar
        val tv_seek = itemView.findViewById<TextView>(R.id.tv_seek)

        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tv_seek.text = "$${AppUtils.getFormattedNumber(minValue.toInt())} to $${AppUtils.getFormattedNumber(maxValue.toInt())}"
            // tv_seek.text = "$" + minValue + "K to " + "$" + maxValue + "K"
        }
    }

}