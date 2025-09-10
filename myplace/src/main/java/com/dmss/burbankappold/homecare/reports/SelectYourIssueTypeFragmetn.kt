package com.dmss.burbankappold.homecare.reports

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.FragmentHomecareDocumentsBinding
import com.dmss.burbankappold.databinding.FragmentReportHomeBinding
import com.dmss.burbankappold.databinding.FragmentSelectYourIssueTypeFragmetnBinding
import com.dmss.burbankappold.homecare.HomeCareBaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectYourIssueTypeFragmetn.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectYourIssueTypeFragmetn : HomeCareBaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSelectYourIssueTypeFragmetnBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelectYourIssueTypeFragmetnBinding.inflate(inflater, container, false)
         initView()
        return binding.root
    }
    private fun initView(){
        binding.close.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btProductType.setOnClickListener {
            findNavController().navigate(R.id.NewIssueFragment)

        }
    }


}