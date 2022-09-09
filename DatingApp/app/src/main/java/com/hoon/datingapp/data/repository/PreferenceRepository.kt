package com.hoon.datingapp.data.repository

interface PreferenceRepository {

    fun putCurrentUserID(uid: String)

    fun getCurrentUserID(): String?

    fun removeCurrentUserID()

}