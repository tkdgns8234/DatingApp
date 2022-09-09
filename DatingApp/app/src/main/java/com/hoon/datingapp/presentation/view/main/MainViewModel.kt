package com.hoon.datingapp.presentation.view.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.data.repository.PreferenceRepositoryImpl
import com.hoon.datingapp.domain.CheckIsNewProfileUseCase
import com.hoon.datingapp.domain.GetUserProfileUseCase
import com.hoon.datingapp.domain.UpdateUserProfileUseCase
import com.hoon.datingapp.domain.UploadPhotoUseCase
import com.hoon.datingapp.presentation.view.BaseViewModel
import com.hoon.datingapp.util.DatabaseResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class MainViewModel(
    private val preferenceRepositoryImpl: PreferenceRepositoryImpl,
    private val checkIsNewProfileUseCase: CheckIsNewProfileUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : BaseViewModel() {

    private var _mainStateLiveData = MutableLiveData<MainState>(MainState.UnInitialized)
    val mainStateLiveData: LiveData<MainState> = _mainStateLiveData

    fun fetchData(): Job = viewModelScope.launch {
        setState(MainState.Loading)

        preferenceRepositoryImpl.getCurrentUserID()?.let {
            setState(MainState.Login)
        } ?: kotlin.run {
            setState(MainState.Logout)
        }
    }

    fun uploadPhoto(uid: String, imageUri: Uri, name: String) = viewModelScope.launch {
        val uri = uploadPhotoUseCase(uid, imageUri)
        setState(MainState.SuccessUpload(uri, name))
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        preferenceRepositoryImpl.removeCurrentUserID()
        setState(MainState.Logout)
    }

    fun updateUserProfile(uid: String, name: String, imageUri: Uri) = viewModelScope.launch {
        updateUserProfileUseCase(uid, name, imageUri)
    }

    fun checkProfileIfNewUser(uid: String) = viewModelScope.launch {
        val dbResponse = checkIsNewProfileUseCase(uid)

        when (dbResponse) {
            is DatabaseResponse.Success<*> -> {
                val isNewProfile = dbResponse.result as Boolean
                handleResult(isNewProfile, uid)
            }
            is DatabaseResponse.Failed -> {
                setState(MainState.Error(ERROR_MESSAGE_LOGIN_FAILED))
            }
        }
    }

    private suspend fun handleResult(isNewProfile: Boolean, uid: String) {
        if (isNewProfile) {
            setState(MainState.SuccessLogin.NewProfile)
        } else {
            val userProfile = getUserProfile(uid)
            userProfile?.let {
                setState(MainState.SuccessLogin.ExistingProfile(userProfile))
            } ?: kotlin.run {
                setState(MainState.SuccessLogin.NewProfile)
            }
        }
    }

    private suspend fun getUserProfile(uid: String): UserProfile? {
        var profile: UserProfile? = null

        viewModelScope.launch {
            val dbResponse = getUserProfileUseCase(uid)
            when (dbResponse) {
                is DatabaseResponse.Success<*> -> {
                    profile = dbResponse.result as? UserProfile
                }
                is DatabaseResponse.Failed -> {
                    setState(MainState.Error(ERROR_MESSAGE_LOAD_PROFILE_FAILED))
                }
            }
        }.join()

        return profile
    }

    fun getCurrentUserID(): String? {
        return preferenceRepositoryImpl.getCurrentUserID()
    }

    private fun setState(state: MainState) {
        _mainStateLiveData.value = state
    }

    companion object {
        const val ERROR_MESSAGE_LOAD_PROFILE_FAILED = "프로필 정보를 불러오는데 실패하였습니다."
        const val ERROR_MESSAGE_LOGIN_FAILED = "로그인에 실패하였습니다."
    }
}