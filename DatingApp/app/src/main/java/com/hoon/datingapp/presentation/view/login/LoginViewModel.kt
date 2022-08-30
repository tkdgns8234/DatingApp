package com.hoon.datingapp.presentation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.presentation.view.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class LoginViewModel(
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {

    private var _loginStatusLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)
    val loginStatusLiveData: LiveData<LoginState> = _loginStatusLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        preferenceManager.getCurrentUserID()?.let {
            setState(LoginState.Success)
        }
    }

    fun putCurrentUserID(userID: String?) {
        userID?.let {
            preferenceManager.putCurrentUserID(userID)
            setState(LoginState.Success)
        } ?: kotlin.run {
            setState(LoginState.Error)
        }

    }

    private fun setState(state: LoginState) {
        _loginStatusLiveData.postValue(state)
        // postValue -> 비동기
    }
}