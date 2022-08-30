package com.hoon.datingapp.data.repository

import android.net.Uri
import com.hoon.datingapp.util.DatabaseResponse

interface FirebaseRepository {
    suspend fun checkIsNewProfile(uid: String): DatabaseResponse

    suspend fun getUserProfile(uid: String): DatabaseResponse

    suspend fun updateUserProfile(uid: String, name: String, imageUri: Uri)

    suspend fun uploadPhotoUri(uid: String, imageUri: Uri) : Uri

}