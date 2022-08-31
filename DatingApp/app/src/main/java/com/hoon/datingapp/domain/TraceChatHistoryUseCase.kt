package com.hoon.datingapp.domain

import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.data.repository.FirebaseRepository

internal class TraceChatHistoryUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(chatKey: String, callback: (message: Message) -> Unit) {
        return repository.traceChatHistory(chatKey, callback)
    }
}