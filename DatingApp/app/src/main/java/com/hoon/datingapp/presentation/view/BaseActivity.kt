package com.hoon.datingapp.presentation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity() {
    protected abstract val viewModel: VM
    private lateinit var job: Job
    protected val binding: VB by lazy { getViewBinding() }

    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeData()
        job = viewModel.fetchData()
    }

    override fun onDestroy() {
        if (job.isActive) { // fetchData 함수에서 viewmodelScope 를 사용하지 않는 경우 active 상태일 수 있음
            job.cancel()
        }

        super.onDestroy()
    }

    abstract fun observeData()
}