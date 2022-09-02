package com.hoon.datingapp.presentation.view.likemelist

import com.hoon.datingapp.data.model.UserProfile

sealed class LikeMeListState {
    object UnInitialized : LikeMeListState()

    object Logout: LikeMeListState()

    data class UpdateUserLikeMe(
        val profile: UserProfile
    ) : LikeMeListState()

    data class Error(
        val message: String
    ) : LikeMeListState()
}