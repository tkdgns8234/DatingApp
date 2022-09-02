package com.hoon.datingapp.presentation.view.likemelist

import androidx.recyclerview.widget.LinearLayoutManager
import com.hoon.datingapp.R
import com.hoon.datingapp.presentation.adapter.LikeMeListAdapter
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.FragmentLikeMeListBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.view.BaseFragment
import com.hoon.datingapp.presentation.view.login.LoginActivity
import org.koin.android.viewmodel.ext.android.viewModel

internal class LikeMeListFragment : BaseFragment<LikeMeListViewModel, FragmentLikeMeListBinding>() {

    override fun getViewBinding() =
        FragmentLikeMeListBinding.inflate(layoutInflater)

    override val viewModel by viewModel<LikeMeListViewModel>()

    private val adapter = LikeMeListAdapter()
    private val userProfiles = mutableListOf<UserProfile>()

    override fun observeData() {
        viewModel.likeMeListStatusLiveData.observe(this) {
            when (it) {
                is LikeMeListState.UnInitialized -> {
                    initViews()
                    viewModel.fetchData()
                }
                is LikeMeListState.UpdateUserLikeMe -> {
                    handleUpdateUserLikeMe(it.profile)
                }
                is LikeMeListState.Logout -> {
                    toast(getString(R.string.status_not_login))
                    startActivity(LoginActivity.newIntent(requireContext()))
                }
                is LikeMeListState.Error -> {}
            }
        }
    }

    private fun handleUpdateUserLikeMe(profile: UserProfile) {
        userProfiles.add(profile)
        adapter.submitList(userProfiles.toMutableList())
    }

    private fun initViews() = with(binding) {
        rvLikeMeList.adapter = adapter
        rvLikeMeList.layoutManager = LinearLayoutManager(context)
    }
}