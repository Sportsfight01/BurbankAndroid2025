package com.dmss.burbankappold.homecare.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalminds.homecare.model.DocumentsDataItem
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.databinding.FragmentHomecareDocumentsBinding
import com.dmss.burbankappold.homecare.HomeCareBaseFragment

class DocumentsHomeCareFragment : HomeCareBaseFragment() {

    private var _binding: FragmentHomecareDocumentsBinding? = null
    private var documentList = mutableListOf<DocumentsDataItem>()
    private var documentsAdapter :  HomeCareDocumentsAdapter? =  null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var documentsList = listOf<DocumentsDataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (documentsAdapter == null){
            documentsAdapter = HomeCareDocumentsAdapter { clickType, document ->

            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomecareDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }
    private fun initViews() {
        documentList.add(DocumentsDataItem("111",false,true,"12-23-1997","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-1998","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-1999","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2000","Documents","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2001","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2002","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2003","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2004","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2005","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2006","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2007","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2008","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2009","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2010","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2011","Document","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2012","digital minds","www","wwqw"))
        documentList.add(DocumentsDataItem("111",false,true,"12-23-2013","Document","www","wwqw"))


        if (documentsAdapter == null){
            documentsAdapter = HomeCareDocumentsAdapter() { clickType, manual ->
                println("clickType:: $clickType")
            }
        }
        _binding!!.rvDocuments.apply {
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