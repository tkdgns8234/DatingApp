package com.hoon.datingapp.domain

import com.hoon.datingapp.data.repository.FirebaseRepository
import com.hoon.datingapp.util.DatabaseResponse

internal class GetMatchedUsersUseCase(
    private val firebaseRepository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(uid: String): DatabaseResponse {
        return firebaseRepository.getMatchedUsers(uid)
    }
}