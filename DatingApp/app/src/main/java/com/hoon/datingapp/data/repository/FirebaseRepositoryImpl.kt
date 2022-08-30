package com.hoon.datingapp.data.repository

import android.net.Uri
import com.hoon.datingapp.data.db.FirebaseRealtimeDB
import com.hoon.datingapp.data.db.FirebaseStorage
import com.hoon.datingapp.util.DatabaseResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FirebaseRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val firebaseDB: FirebaseRealtimeDB,
    private val storage: FirebaseStorage
) : FirebaseRepository {

    override suspend fun checkIsNewProfile(uid: String): DatabaseResponse = withContext(ioDispatcher) {
        firebaseDB.checkIsNewProfile(uid)
    }

    override suspend fun getUserProfile(uid: String): DatabaseResponse = withContext(ioDispatcher) {
        firebaseDB.getUserProfile(uid)
    }

    override suspend fun updateUserProfile(uid: String, name: String, imageUri: Uri) = withContext(ioDispatcher) {
        firebaseDB.updateUserProfile(uid, name, imageUri)
    }

    override suspend fun uploadPhotoUri(uid: String, imageUri: Uri): Uri = withContext(ioDispatcher) {
        storage.uploadPhoto(uid, imageUri)
    }
}