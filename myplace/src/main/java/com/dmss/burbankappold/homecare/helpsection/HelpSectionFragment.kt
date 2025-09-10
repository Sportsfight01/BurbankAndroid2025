package com.dmss.burbankappold.homecare.helpsection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalminds.homecare.model.HelpSectionDataItem
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.databinding.FragmentHelpSectionBinding
import com.dmss.burbankappold.homecare.HomeCareBaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HelpSectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HelpSectionFragment : HomeCareBaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentHelpSectionBinding? = null
    private var manualsAdapter :  HelpSectionAdapter? =  null
    private var manualList = mutableListOf<HelpSectionDataItem>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHelpSectionBinding.inflate(inflater, container, false)
        println("FragmentManualsBinding::")
        initView()

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun initView(){
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-1998","Help section","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-1998","Help section","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-1999","Help section","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2000","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2001","Help section","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2002","Help section","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2003","Help section","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2004","Help section","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2005","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2006","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2007","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2008","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2009","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2010","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2011","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2012","digital minds","www","wwqw"))
        manualList.add(HelpSectionDataItem("111",false,true,"12-23-2013","digital minds","www","wwqw"))


        if (manualsAdapter == null){
            manualsAdapter = HelpSectionAdapter() { clickType, _ ->
                println("clickType:: $clickType")
            }
        }
        _binding!!.rvHelpSection.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = manualsAdapter
        }
        manualsAdapter!!.setDocumentsList(manualList)

    }

}