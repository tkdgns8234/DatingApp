package com.hoon.datingapp.presentation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hoon.datingapp.data.repository.PreferenceRepositoryImpl
import com.hoon.datingapp.presentation.view.BaseViewModel
import kotlinx.coroutines.launch

internal class SignUpViewModel(
    private val preferenceRepositoryImpl: PreferenceRepositoryImpl
) : BaseViewModel() {

    private val _signUpStateLiveData = MutableLiveData<SignUpState>(SignUpState.Uninitialized)
    val signUpStateLiveData: LiveData<SignUpState> = _signUpStateLiveData

    val auth by lazy { FirebaseAuth.getInstance() }

    fun login(email: String, pw: String) = viewModelScope.launch {
        auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setState(SignUpState.Success.Login)
            } else {
                setState(SignUpState.Error(ERROR_MSG_LOGIN_FAILED))
            }
        }
    }

    fun signUp(email: String, pw: String) = viewModelScope.launch {
        auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setState(SignUpState.Success.SignUp)
            } else {
                setState(SignUpState.Error(ERROR_MSG_SIGNUP_FAILED))
            }
        }
    }

    fun putCurrentUserID() = viewModelScope.launch {
        val uid = auth.uid
        uid?.let {
            preferenceRepositoryImpl.putCurrentUserID(uid)
        }
    }

    private fun setState(state: SignUpState) {
        _signUpStateLiveData.value = state
    }

    companion object {
        const val ERROR_MSG_LOGIN_FAILED = "로그인에 실패했습니다, 이메일과 패스워드를 확인해주세요"
        const val ERROR_MSG_SIGNUP_FAILED = "이미 가입된 회원이거나 이메일 형식이 잘못되었습니다."
    }
}