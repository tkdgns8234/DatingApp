package com.hoon.datingapp.presentation.view.chatlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.domain.GetMatchedUsersUseCase
import com.hoon.datingapp.presentation.view.BaseViewModel
import com.hoon.datingapp.presentation.view.main.MainState
import com.hoon.datingapp.presentation.view.main.MainViewModel
import com.hoon.datingapp.util.DatabaseResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ChatListViewModel(
    private val preferenceManager: PreferenceManager,
    private val getMatchedUsersUseCase: GetMatchedUsersUseCase
) : BaseViewModel() {

    private var _chatListStatusLiveData =
        MutableLiveData<ChatListState>(ChatListState.UnInitialized)
    val chatListStatusLiveData: LiveData<ChatListState> = _chatListStatusLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        getMatchedUsers()
    }

    fun getMatchedUsers() = viewModelScope.launch {
        val uid = getCurrentUserID()

        uid?.let {
            val dbResponse = getMatchedUsersUseCase(it)

            when (dbResponse) {
                is DatabaseResponse.Success<*> -> {
                    val chatList = dbResponse.result as List<ChatRoom>
                    setState(ChatListState.GetMatchedUsers(chatList))
                }
                is DatabaseResponse.Failed -> {
                    setState(ChatListState.Error(ERROR_MESSAGE_GET_MATCHED_USERS_FAILED))
                }
            }
        } ?: kotlin.run {
            setState(ChatListState.Error(ERROR_MESSAGE_GET_MATCHED_USERS_FAILED))
        }
    }

    fun getCurrentUserID(): String? {
        return preferenceManager.getCurrentUserID()
    }

    private fun setState(state: ChatListState) {
        _chatListStatusLiveData.postValue(state)
    }

    companion object {
        const val ERROR_MESSAGE_GET_MATCHED_USERS_FAILED = "매칭된 유저 탐색에 실패하였습니다."
    }
}