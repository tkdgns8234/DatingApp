package com.hoon.datingapp.domain

import android.net.Uri
import com.hoon.datingapp.data.repository.FirebaseRepository

internal class UpdateUserProfileUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(uid: String, name: String, imageUri: Uri) {
        repository.updateUserProfile(uid, name, imageUri)
    }
}