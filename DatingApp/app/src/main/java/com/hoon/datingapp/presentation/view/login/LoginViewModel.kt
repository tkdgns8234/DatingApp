package com.hoon.datingapp.presentation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hoon.datingapp.data.repository.PreferenceRepositoryImpl
import com.hoon.datingapp.presentation.view.BaseViewModel

internal class LoginViewModel(
    private val preferenceRepositoryImpl: PreferenceRepositoryImpl
) : BaseViewModel() {

    private var _loginStatusLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)
    val loginStatusLiveData: LiveData<LoginState> = _loginStatusLiveData

    fun putCurrentUserID(userID: String?) {
        userID?.let {
            preferenceRepositoryImpl.putCurrentUserID(userID)
            setState(LoginState.Success)
        } ?: kotlin.run {
            setState(LoginState.Error)
        }

    }

    private fun setState(state: LoginState) {
        _loginStatusLiveData.value = state
    }
}