package com.dmss.burbankapp.ui.designs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment

import com.dmss.burbankapp.R

/**
 * A simple [Fragment] subclass.
 */
class MyCollectionsBottomSheetFragment : DialogFragment() {
    lateinit var itemView: View

    fun newInstance(title: String?): MyCollectionsBottomSheetFragment? {
        val frag = MyCollectionsBottomSheetFragment()
        val args = Bundle()
        args.putString("title", title)
        frag.arguments = args
        return frag
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        itemView= inflater.inflate(R.layout.fragment_my_collections_bottom_sheet, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inItView()
    }




    private fun inItView() {
        var closeIcon = itemView.findViewById<ImageView>(R.id.iv_close)
        var rl_sort_by = itemView.findViewById<RelativeLayout>(R.id.rl_sort_by)
        closeIcon.setOnClickListener {
            dialog?.dismiss()
        }

        /*rl_sort_by.setOnClickListener {
            showBottomSheetFilter()
        }*/
        setRangeSeekbar()
    }
    private fun setRangeSeekbar() {

//        val rangeSeekbar =itemView.findViewById(R.id.seekbar) as CrystalRangeSeekbar
        val tv_seek = itemView.findViewById<TextView>(R.id.tv_seek)


        // set listener
       /* rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tv_seek.text ="$"+minValue+"K to "+"$"+maxValue+"K"

        }*/
    }

    /*fun showBottomSheetFilter(){
        val fm: FragmentManager = activity!!.supportFragmentManager
        var fragment = HomeLandFilterBottomSheetFragment().newInstance();
        fragment?.show(fm, "fragment_filter_name")

    }*/

}
