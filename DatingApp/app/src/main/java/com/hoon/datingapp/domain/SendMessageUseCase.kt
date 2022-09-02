package com.hoon.datingapp.domain

import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.data.repository.FirebaseRepository

internal class SendMessageUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(chatKey: String, message: Message) {
        return repository.sendMessage(chatKey, message)
    }
}