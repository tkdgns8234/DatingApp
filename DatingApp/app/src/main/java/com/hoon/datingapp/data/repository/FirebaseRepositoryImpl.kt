package com.hoon.datingapp.data.repository

import android.net.Uri
import com.hoon.datingapp.data.db.FirebaseRealtimeDB
import com.hoon.datingapp.data.db.FirebaseStorage
import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.data.model.UserProfile
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

    override suspend fun sendMessage(chatKey: String, message: Message) = withContext(ioDispatcher) {
        firebaseDB.sendMessage(chatKey, message)
    }

    override suspend fun traceChatHistory(chatKey: String, callback: (message: Message) -> Unit) = withContext(ioDispatcher) {
        firebaseDB.traceChatHistory(chatKey, callback)
    }

    override suspend fun getMatchedUsers(uid: String): DatabaseResponse = withContext(ioDispatcher) {
        firebaseDB.getMatchedUsers(uid)
    }

    override suspend fun traceUsersLikeMe(uid: String, callback: (profile: UserProfile) -> Unit) = withContext(ioDispatcher) {
        firebaseDB.traceUsersLikeMe(uid, callback)
    }

    override suspend fun traceNewUserAndChangedUser(
        uid: String,
        newUserCallback: (UserProfile) -> Unit,
        changedUserCallback: (UserProfile) -> Unit
    ) = withContext(ioDispatcher) {
        firebaseDB.traceNewUserAndChangedUser(uid, newUserCallback, changedUserCallback)
    }

    override suspend fun like(currentUserID: String, otherUserID: String) = withContext(ioDispatcher) {
        firebaseDB.like(currentUserID, otherUserID)
    }

    override suspend fun disLike(currentUserID: String, otherUserID: String) = withContext(ioDispatcher) {
        firebaseDB.disLike(currentUserID, otherUserID)
    }

    override suspend fun makeChatRoomIfLikeEachOther(currentUserID: String, otherUserId: String) = withContext(ioDispatcher) {
        firebaseDB.makeChatRoomIfLikeEachOther(currentUserID, otherUserId)
    }

    override suspend fun uploadPhotoUri(uid: String, imageUri: Uri): Uri = withContext(ioDispatcher) {
        storage.uploadPhoto(uid, imageUri)
    }
}