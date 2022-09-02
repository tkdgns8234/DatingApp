package com.hoon.datingapp.domain

import com.hoon.datingapp.data.repository.FirebaseRepository

internal class DisLikeUserUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(currentUserID: String, otherUserID: String) {
        return repository.disLike(currentUserID, otherUserID)
    }
}