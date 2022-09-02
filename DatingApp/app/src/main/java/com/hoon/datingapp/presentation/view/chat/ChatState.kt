package com.hoon.datingapp.presentation.view.chat

import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.presentation.view.login.LoginState

sealed class ChatState {

    object UnInitialized : ChatState()

    object Logout : ChatState()

    sealed class Success : ChatState() {
        data class UpdatePartnerUserProfile(val profile: UserProfile) : Success()
    }

    data class Error(
        val message: String
    ) : ChatState()

}