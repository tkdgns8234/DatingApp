package com.hoon.datingapp.presentation.view.main

import android.net.Uri
import com.hoon.datingapp.data.model.UserProfile

sealed class MainState {
    object UnInitialized : MainState()

    object Loading : MainState()

    object Login : MainState()

    object Logout : MainState()

    sealed class SuccessLogin : MainState() {
        object NewProfile : SuccessLogin()
        data class ExistingProfile(
            val userProfile: UserProfile
        ) : SuccessLogin()
    }

    data class SuccessUpload(
        val imageUri: Uri,
        val name: String
    ) : MainState()

    data class Error(
        val message: String
    ) : MainState()
}