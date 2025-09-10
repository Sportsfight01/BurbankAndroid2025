package com.dmss.burbankapp.ui.designs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction

import com.dmss.burbankapp.R

/**
 * A simple [Fragment] subclass.
 */
class MyCollectionFifthFragment : Fragment() {
    lateinit var itemView:View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        itemView = inflater.inflate(R.layout.fragment_my_collection_fifth, container, false)
        return itemView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val backIcon = itemView.findViewById<ImageView>(R.id.iv_back)
        val ll_yes = itemView.findViewById<LinearLayout>(R.id.ll_yes)
        val tv_designs = itemView.findViewById<TextView>(R.id.tv_designs)

        val tv_tool = itemView.findViewById<TextView>(R.id.tv_tool)
        val tv_how_does_it = itemView.findViewById<TextView>(R.id.tv_how_does_it)
        tv_tool.setText("< 10.5m, SINGLE $150K-$290K")
        tv_how_does_it.visibility = View.INVISIBLE

        tv_designs.setOnClickListener {
            var fragment = MyCollectionPlacesFragment();
            loadFragment(fragment)
        }
        /*val tvContinue = itemView.findViewById<TextView>(R.id.tv_continue)
        tvContinue.setOnClickListener {
            var fragment = MyCollectionSecondFragment();
            loadFragment(fragment)
        }*/
        ll_yes.setOnClickListener {
            var fragment = BedroomsMyCollectionsFragment();
            loadFragment(fragment)
        }
        backIcon.setOnClickListener {
            activity!!.onBackPressed()
        }
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

}
