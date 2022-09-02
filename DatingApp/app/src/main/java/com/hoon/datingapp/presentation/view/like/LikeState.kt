package com.hoon.datingapp.presentation.view.like

import com.hoon.datingapp.data.model.UserProfile

sealed class LikeState {

    object UnInitialized : LikeState()

    object Logout : LikeState()

    sealed class Callback {
        data class NewUser(
            val profile: UserProfile
        ) : LikeState()

        data class ChangedUser(
            val profile: UserProfile
        ) : LikeState()
    }

    object Success

    data class Error(
        val message: String
    ) : LikeState()
}