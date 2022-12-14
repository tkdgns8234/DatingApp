package com.hoon.datingapp.presentation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.presentation.view.BaseViewModel

internal class LoginViewModel(
    private val preferenceManager: PreferenceManager
    ) : BaseViewModel() {

    private var _loginStatusLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)
    val loginStatusLiveData: LiveData<LoginState> = _loginStatusLiveData

    fun putCurrentUserID(userID: String?) {
        userID?.let {
            preferenceManager.putCurrentUserID(userID)
            setState(LoginState.Success)
        } ?: kotlin.run {
            setState(LoginState.Error)
        }

    }

    private fun setState(state: LoginState) {
        _loginStatusLiveData.value = state
    }
}