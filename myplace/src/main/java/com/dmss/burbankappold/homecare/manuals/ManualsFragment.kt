package com.dmss.burbankappold.homecare.manuals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalminds.homecare.model.ManualDataIteam
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.databinding.FragmentManualsBinding
import com.dmss.burbankappold.homecare.HomeCareBaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManualsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManualsFragment : HomeCareBaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentManualsBinding? = null
    private var manualsAdapter :  ManualsAdapter? =  null
    private var manualList = mutableListOf<ManualDataIteam>()

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        _binding = FragmentManualsBinding.inflate(inflater, container, false)
        println("FragmentManualsBinding::")
        initView()

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun initView(){
      manualList.add(ManualDataIteam("111",false,true,"12-23-1998","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-1998","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-1999","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2000","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2001","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2002","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2003","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2004","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2005","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2006","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2007","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2008","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2009","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2010","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2011","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2012","digital minds","www","wwqw"))
        manualList.add(ManualDataIteam("111",false,true,"12-23-2013","digital minds","www","wwqw"))


        if (manualsAdapter == null){
            manualsAdapter = ManualsAdapter() {clickType, manual ->
                println("clickType:: $clickType")
            }
        }
        _binding!!.rvManuals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = manualsAdapter
        }
        manualsAdapter!!.setDocumentsList(manualList)

    }

}