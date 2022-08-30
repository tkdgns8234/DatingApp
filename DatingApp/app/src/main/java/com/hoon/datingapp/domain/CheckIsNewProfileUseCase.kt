package com.hoon.datingapp.domain

import com.hoon.datingapp.data.repository.FirebaseRepository
import com.hoon.datingapp.util.DatabaseResponse

internal class CheckIsNewProfileUseCase(
    private val firebaseRepository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(uid: String): DatabaseResponse {
        return firebaseRepository.checkIsNewProfile(uid)
    }
}