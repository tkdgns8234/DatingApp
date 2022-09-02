package com.hoon.datingapp.data.repository

import android.net.Uri
import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.util.DatabaseResponse

interface FirebaseRepository {
    suspend fun checkIsNewProfile(uid: String): DatabaseResponse

    suspend fun getUserProfile(uid: String): DatabaseResponse

    suspend fun updateUserProfile(uid: String, name: String, imageUri: Uri)

    suspend fun uploadPhotoUri(uid: String, imageUri: Uri): Uri

    suspend fun sendMessage(chatKey: String, message: Message)

    suspend fun traceChatHistory(chatKey: String, callback: (message: Message) -> Unit)

    suspend fun getMatchedUsers(uid: String): DatabaseResponse

    suspend fun traceUsersLikeMe(uid: String, callback: (profile: UserProfile) -> Unit)

    suspend fun traceNewUserAndChangedUser(
        uid: String,
        newUserCallback: (UserProfile) -> Unit,
        changedUserCallback: (UserProfile) -> Unit
    )

    suspend fun like(currentUserID: String, otherUserID: String)

    suspend fun disLike(currentUserID: String, otherUserID: String)

    suspend fun makeChatRoomIfLikeEachOther(currentUserID: String, otherUserId: String)
}