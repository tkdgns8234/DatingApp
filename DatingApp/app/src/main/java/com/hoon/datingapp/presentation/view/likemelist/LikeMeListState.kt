package com.hoon.datingapp.presentation.view.likemelist

sealed class LikeMeListState {
    object UnInitialized : LikeMeListState()

    data class Error(
        val message: String
    ) : LikeMeListState()
}