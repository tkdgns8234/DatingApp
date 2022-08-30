package com.hoon.datingapp.domain

import android.net.Uri
import com.hoon.datingapp.data.repository.FirebaseRepository

internal class UploadPhotoUseCase(
    private val repository: FirebaseRepository
) : UseCase {

    suspend operator fun invoke(uid: String, imageUri: Uri): Uri {
        return repository.uploadPhotoUri(uid, imageUri)
    }
}