package com.hoon.datingapp.domain

import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.data.repository.FirebaseRepository

internal class TraceUsersLikeMeUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(uid: String, callback: (profile: UserProfile) -> Unit) {
        return repository.traceUsersLikeMe(uid, callback)
    }
}