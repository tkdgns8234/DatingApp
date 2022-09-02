package com.hoon.datingapp.domain

import com.hoon.datingapp.data.repository.FirebaseRepository

internal class MakeChatRoomIfLikeEachOtherUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(currentUserID: String, otherUserID: String) {
        return repository.makeChatRoomIfLikeEachOther(currentUserID, otherUserID)
    }
}