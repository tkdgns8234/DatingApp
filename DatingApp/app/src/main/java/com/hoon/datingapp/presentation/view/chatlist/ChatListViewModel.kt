package com.hoon.datingapp.presentation.view.chatlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.data.repository.PreferenceRepositoryImpl
import com.hoon.datingapp.domain.GetMatchedUsersUseCase
import com.hoon.datingapp.domain.GetUserProfileUseCase
import com.hoon.datingapp.presentation.view.BaseViewModel
import com.hoon.datingapp.util.DatabaseResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ChatListViewModel(
    private val preferenceRepositoryImpl: PreferenceRepositoryImpl,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getMatchedUsersUseCase: GetMatchedUsersUseCase
) : BaseViewModel() {

    private var _chatListStatusLiveData =
        MutableLiveData<ChatListState>(ChatListState.UnInitialized)
    val chatListStatusLiveData: LiveData<ChatListState> = _chatListStatusLiveData

    fun fetchData(): Job = viewModelScope.launch {
        getMatchedUsers()
    }

    fun getMatchedUsers() = viewModelScope.launch {
        val uid = getCurrentUserID()

        uid?.let {
            val dbResponse = getMatchedUsersUseCase(it)

            when (dbResponse) {
                is DatabaseResponse.Success<*> -> {
                    val chatList = dbResponse.result as? List<ChatRoom> ?: return@launch
                    setState(ChatListState.GetMatchedUsers(chatList))
                }
                is DatabaseResponse.Failed -> {
                    setState(ChatListState.Error(ERROR_MESSAGE_GET_MATCHED_USERS_FAILED))
                }
            }
        } ?: kotlin.run {
            setState(ChatListState.Logout)
        }
    }

    fun getUserProfile(uid: String, completeHandler: (name: String, imageURI: String) -> Unit) =
        viewModelScope.launch {
            val dbResponse = getUserProfileUseCase(uid)
            when (dbResponse) {
                is DatabaseResponse.Success<*> -> {
                    val profile = dbResponse.result as? UserProfile ?: UserProfile()

                    completeHandler(profile.userName, profile.imageURI)
                }
                is DatabaseResponse.Failed -> {
                    setState(ChatListState.Error(ERROR_MESSAGE_LOAD_PROFILE_FAILED))
                }
            }
        }

    fun getCurrentUserID(): String? {
        return preferenceRepositoryImpl.getCurrentUserID()
    }

    private fun setState(state: ChatListState) {
        _chatListStatusLiveData.value = state
    }

    companion object {
        const val ERROR_MESSAGE_LOAD_PROFILE_FAILED = "프로필 정보를 불러오는데 실패하였습니다."
        const val ERROR_MESSAGE_GET_MATCHED_USERS_FAILED = "매칭된 유저 탐색에 실패하였습니다."
    }
}