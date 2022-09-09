package com.hoon.datingapp.presentation.view.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoon.datingapp.data.repository.PreferenceRepositoryImpl
import com.hoon.datingapp.domain.DisLikeUserUseCase
import com.hoon.datingapp.domain.LikeUserUseCase
import com.hoon.datingapp.domain.MakeChatRoomIfLikeEachOtherUseCase
import com.hoon.datingapp.domain.TraceNewUserAndChangedUserUseCase
import com.hoon.datingapp.presentation.view.BaseViewModel
import kotlinx.coroutines.launch

internal class LikeViewModel(
    private val preferenceRepositoryImpl: PreferenceRepositoryImpl,
    private val traceNewUserAndChangedUserUseCase: TraceNewUserAndChangedUserUseCase,
    private val likeUserUseCase: LikeUserUseCase,
    private val disLikeUserUseCase: DisLikeUserUseCase,
    private val makeChatRoomIfLikeEachOtherUseCase: MakeChatRoomIfLikeEachOtherUseCase
) : BaseViewModel() {

    private var _likeStatusLiveData = MutableLiveData<LikeState>(LikeState.UnInitialized)
    val likeStatusLiveData: LiveData<LikeState> = _likeStatusLiveData

    fun fetchData() = viewModelScope.launch {
        traceNewUserAndChangedUser()
    }

    fun like(otherUserID: String) = viewModelScope.launch {
        val uid = getCurrentUserID()

        uid?.let { uid ->
            likeUserUseCase(currentUserID = uid, otherUserID = otherUserID)

            //서로 like 하여 매칭되었으면 채팅방 생성
            makeChatRoomIfLikeEachOtherUseCase(uid, otherUserID)
        }
    }

    fun disLike(otherUserID: String) = viewModelScope.launch {
        val uid = getCurrentUserID()

        uid?.let {
            disLikeUserUseCase(currentUserID = it, otherUserID = otherUserID)
        }
    }

    fun traceNewUserAndChangedUser() = viewModelScope.launch {
        val uid = getCurrentUserID()

        uid?.let {
            traceNewUserAndChangedUserUseCase(
                it,
                newUserCallback = { profile ->
                    setState(LikeState.Callback.NewUser(profile))
                },
                changedUserCallback = { profile ->
                    setState(LikeState.Callback.ChangedUser(profile))
                }
            )

        } ?: kotlin.run {
            setState(LikeState.Logout)
        }
    }

    fun getCurrentUserID(): String? {
        return preferenceRepositoryImpl.getCurrentUserID()
    }

    private fun setState(state: LikeState) {
        _likeStatusLiveData.value = state
    }
}