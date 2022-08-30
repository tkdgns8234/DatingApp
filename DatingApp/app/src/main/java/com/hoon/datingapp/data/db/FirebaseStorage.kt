package com.hoon.datingapp.data.db

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseStorage {
    companion object {
        const val FIREBASE_STORAGE_PATH_IMAGES = "profile/images"
    }

    private val storage = Firebase.storage

    internal suspend fun uploadPhoto(uid: String, imageUri: Uri): Uri {
        val fileName = "${uid}_${System.currentTimeMillis()}.png"
        return storage.reference.child(FIREBASE_STORAGE_PATH_IMAGES).child(fileName)
            .putFile(imageUri)
            .await() // wait putFile
            .storage
            .downloadUrl
            .await() // wait downloadUrl
    }
}