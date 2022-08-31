package com.hoon.datingapp.domain

import com.hoon.datingapp.data.repository.FirebaseRepository

internal class SendMessageUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(chatKey: String, message: String) {
        return repository.sendMessage(chatKey, message)
    }
}