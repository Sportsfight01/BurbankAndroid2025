package com.dmss.burbankapp.ui.mydisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.dmss.burbankapp.ui.main.MainActivity

abstract class BaseContainerFragment<B : ViewBinding> : Fragment() {
    private lateinit var binding: B
    var toolText: String = ""
    abstract fun initUi()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(layoutInflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        updateToolText(toolText)
    }

    abstract fun getFragmentBinding(layoutInflater: LayoutInflater, container: ViewGroup?): B

     fun updateToolText(content: String) {
        if (content.isNotEmpty()) {
            (activity as MainActivity).changeHelpText(content)
        }

    }


}