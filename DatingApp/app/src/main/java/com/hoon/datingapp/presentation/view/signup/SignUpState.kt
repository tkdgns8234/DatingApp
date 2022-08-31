package com.hoon.datingapp.presentation.view.signup

import com.hoon.datingapp.presentation.view.main.MainState

sealed class SignUpState {
    object Uninitialized: SignUpState()

    sealed class Success: SignUpState() {
        object SignUp: Success()
        object Login: Success()
    }

    data class Error(
        val message: String
    ): SignUpState()

}