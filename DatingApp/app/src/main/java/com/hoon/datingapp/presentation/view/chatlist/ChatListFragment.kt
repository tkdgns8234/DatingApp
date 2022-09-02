package com.hoon.datingapp.presentation.view.chatlist

import androidx.recyclerview.widget.LinearLayoutManager
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.databinding.FragmentChatListBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.adapter.ChatListAdapter
import com.hoon.datingapp.presentation.view.BaseFragment
import com.hoon.datingapp.presentation.view.chat.ChatActivity
import com.hoon.datingapp.presentation.view.login.LoginActivity
import org.koin.android.viewmodel.ext.android.viewModel

internal class ChatListFragment : BaseFragment<ChatListViewModel, FragmentChatListBinding>() {

    override fun getViewBinding(): FragmentChatListBinding {
        return FragmentChatListBinding.inflate(layoutInflater)
    }

    override val viewModel by viewModel<ChatListViewModel>()

    private val adapter by lazy {
        ChatListAdapter(onClickListener, getUserProfileHandler)
    }

    override fun observeData() {
        viewModel.chatListStatusLiveData.observe(this) {
            when (it) {
                is ChatListState.UnInitialized -> {
                    initViews()
                    viewModel.fetchData()
                }
                is ChatListState.GetMatchedUsers -> {
                    handleGetMatchedUsers(it.chatList)
                }
                is ChatListState.Logout -> {
                    toast(getString(R.string.status_not_login))
                    startActivity(LoginActivity.newIntent(requireContext()))
                }
                is ChatListState.Error -> {
                    handleErrorState(it.message)
                }
            }
        }
    }

    private fun handleErrorState(msg: String) {
        toast(msg)
    }

    private fun handleGetMatchedUsers(chatList: List<ChatRoom>) {
        adapter.submitList(chatList)
    }

    private fun initViews() = with(binding) {
        rvChatList.adapter = adapter
        rvChatList.layoutManager = LinearLayoutManager(context)
    }

    private val onClickListener: (String, String) -> Unit = { partnerID: String, chatKey: String ->
        val intent =
            ChatActivity.newIntent(requireContext(), chatKey = chatKey, partnerID = partnerID)
        startActivity(intent)
    }

    private val getUserProfileHandler: (String, completeHandler: (String, String) -> Unit) -> Unit =
        { userID, completeHandler ->
            viewModel.getUserProfile(userID, completeHandler)
        }
}