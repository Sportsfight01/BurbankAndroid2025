package com.dmss.burbankappold.homecare.warranties

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalminds.homecare.model.DocumentsDataItem
import com.digitalminds.homecare.model.WarrantiesDataItem
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.databinding.FragmentWarrantiesBinding
import com.dmss.burbankappold.homecare.HomeCareBaseFragment
import com.dmss.burbankappold.homecare.warranties.WarrantiesAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WarrantiesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WarrantiesFragment : HomeCareBaseFragment() {

    private var _binding: FragmentWarrantiesBinding? = null
    private var documentList = mutableListOf<WarrantiesDataItem>()
    private var documentsAdapter :  WarrantiesAdapter? =  null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var documentsList = listOf<DocumentsDataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (documentsAdapter == null){
            documentsAdapter = WarrantiesAdapter {clickType, document ->

            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWarrantiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }
    private fun initViews() {
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-1998","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-1998","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-1999","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2000","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2001","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2002","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2003","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2004","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2005","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2006","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2007","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2008","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2009","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2010","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2011","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2012","Warranties","www","wwqw"))
        documentList.add(WarrantiesDataItem("111",false,true,"12-23-2013","Warranties","www","wwqw"))


        if (documentsAdapter == null){
            documentsAdapter = WarrantiesAdapter() {clickType, manual ->
                println("clickType:: $clickType")
            }
        }
        _binding!!.rvWarranties.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = documentsAdapter
        }
        documentsAdapter!!.setDocumentsList(documentList)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}