package com.hoon.datingapp.presentation.view.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.data.preference.PreferenceManager
import com.hoon.datingapp.domain.GetUserProfileUseCase
import com.hoon.datingapp.domain.SendMessageUseCase
import com.hoon.datingapp.domain.TraceChatHistoryUseCase
import com.hoon.datingapp.presentation.view.BaseViewModel
import com.hoon.datingapp.util.DatabaseResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ChatViewModel(
    private val preferenceManager: PreferenceManager,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val traceChatHistoryUseCase: TraceChatHistoryUseCase
) : BaseViewModel() {

    private var _chatStateLiveData = MutableLiveData<ChatState>(ChatState.UnInitialized)
    val chatStateLiveData: LiveData<ChatState> = _chatStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
    }

    fun getPartnerUserProfile(uid: String) = viewModelScope.launch {
        val dbResponse = getUserProfileUseCase(uid)
        when (dbResponse) {
            is DatabaseResponse.Success<*> -> {
                val profile = dbResponse.result as? UserProfile
                profile?.let {
                    setState(ChatState.Success.UpdatePartnerUserProfile(profile))
                } ?: kotlin.run {
                    setState(ChatState.Error(ERROR_MESSAGE_LOAD_PROFILE_FAILED))
                }
            }
            is DatabaseResponse.Failed -> {
                setState(ChatState.Error(ERROR_MESSAGE_LOAD_PROFILE_FAILED))
            }
        }
    }

    fun traceChatHistory(chatKey: String, callback: (message: Message) -> Unit) =
        viewModelScope.launch {
            traceChatHistoryUseCase(chatKey, callback)
        }

    fun sendMessage(chatKey: String, msg: String) = viewModelScope.launch {
        val uid = getCurrentUserID()

        uid?.let {
            Message(uid, msg, System.currentTimeMillis())
            sendMessageUseCase(chatKey = chatKey, message = msg)
        }
    }

    private fun setState(state: ChatState) {
        _chatStateLiveData.postValue(state)
    }

    fun getCurrentUserID(): String? {
        return preferenceManager.getCurrentUserID()
    }

    companion object {
        const val ERROR_MESSAGE_LOAD_PROFILE_FAILED = "프로필 정보를 불러오는데 실패하였습니다."
    }
}