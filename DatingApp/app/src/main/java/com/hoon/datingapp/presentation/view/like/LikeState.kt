package com.hoon.datingapp.presentation.view.like

sealed class LikeState {
    object UnInitialized : LikeState()

    data class Error(
        val message: String
    ) : LikeState()
}