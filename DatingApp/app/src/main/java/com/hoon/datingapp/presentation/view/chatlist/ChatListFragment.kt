package com.hoon.datingapp.presentation.view.chatlist

import androidx.recyclerview.widget.LinearLayoutManager
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.databinding.FragmentChatListBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.adapter.ChatListAdapter
import com.hoon.datingapp.presentation.view.BaseFragment
import com.hoon.datingapp.presentation.view.chat.ChatActivity
import org.koin.android.viewmodel.ext.android.viewModel

internal class ChatListFragment : BaseFragment<ChatListViewModel, FragmentChatListBinding>() {

    override fun getViewBinding(): FragmentChatListBinding {
        return FragmentChatListBinding.inflate(layoutInflater)
    }

    override val viewModel by viewModel<ChatListViewModel>()

    private var adapter = ChatListAdapter()

    override fun observeData() {
        viewModel.chatListStatusLiveData.observe(this) {
            when (it) {
                is ChatListState.UnInitialized -> {
                    initViews()
                }
                is ChatListState.GetMatchedUsers -> {
                    handleGetMatchedUsers(it.chatList)
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

        adapter.setOnClickListener { partnerID, chatKey ->
            val intent =
                ChatActivity.newIntent(requireContext(), chatKey = chatKey, partnerID = partnerID)
            startActivity(intent)
        }
    }
}