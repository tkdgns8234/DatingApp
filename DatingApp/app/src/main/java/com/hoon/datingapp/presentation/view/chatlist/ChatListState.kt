package com.hoon.datingapp.presentation.view.chatlist

import com.hoon.datingapp.data.model.ChatRoom

sealed class ChatListState {

    object UnInitialized : ChatListState()

    object Logout: ChatListState()

    data class GetMatchedUsers(
        val chatList: List<ChatRoom>
    ): ChatListState()

    data class Error(
        val message: String
    ) : ChatListState()
}