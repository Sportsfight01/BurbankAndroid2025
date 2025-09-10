package com.dmss.burbankapp.ui.designs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dmss.burbankapp.R

/**
 * A simple [Fragment] subclass.
 */
class MyCollectionFavDesignFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_collection_fav_design, container, false)
    }

}
