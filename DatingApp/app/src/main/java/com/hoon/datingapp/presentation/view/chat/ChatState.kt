package com.hoon.datingapp.presentation.view.chat

import com.hoon.datingapp.presentation.view.login.LoginState

sealed class ChatState {

    object Uninitialized : ChatState()

    sealed class Success : ChatState() {
        object SendMessage : Success()
        object GetPartnerImageUri: Success()
    }

    data class Error(
        val message: String
    ) : ChatState()

}