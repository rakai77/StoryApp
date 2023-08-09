package com.example.storyapp.common.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(
    private val inflaterFactory: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding : VB? = null
    protected val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflaterFactory(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", this::class.simpleName.toString())

        initInteraction()
        initObservation()
    }

    abstract fun initInteraction()

    abstract fun initObservation()

    open fun onConnect() {
        Log.d("TAG", "onConnect: ")
    }

    open fun onDisconnect() {
        Log.d("TAG", "onDisconnect: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}