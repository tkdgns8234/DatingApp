package com.hoon.datingapp.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.hoon.datingapp.databinding.FragmentLikeBinding
import kotlinx.coroutines.Job

abstract class BaseFragment<VM: BaseViewModel, VB: ViewBinding> : Fragment() {

    private var _binding: VB? = null // 메모리 leak 방지
    protected val binding get() = _binding!! // null 체크 없이 binding 객체에 접근하기 위함

    abstract val viewModel: VM

    abstract fun getViewBinding(): VB

    abstract fun observeData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}