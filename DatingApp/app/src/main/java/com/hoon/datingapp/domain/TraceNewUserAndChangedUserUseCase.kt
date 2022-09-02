package com.hoon.datingapp.domain

import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.data.repository.FirebaseRepository

internal class TraceNewUserAndChangedUserUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(
        uid: String,
        newUserCallback: (UserProfile) -> Unit,
        changedUserCallback: (UserProfile) -> Unit
    ) {
        return repository.traceNewUserAndChangedUser(uid, newUserCallback, changedUserCallback)
    }
}