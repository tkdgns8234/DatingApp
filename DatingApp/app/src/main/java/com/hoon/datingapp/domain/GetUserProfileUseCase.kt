package com.hoon.datingapp.domain

import com.hoon.datingapp.data.repository.FirebaseRepository
import com.hoon.datingapp.util.DatabaseResponse

internal class GetUserProfileUseCase(
    val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(uid: String): DatabaseResponse {
        return repository.getUserProfile(uid)
    }
}