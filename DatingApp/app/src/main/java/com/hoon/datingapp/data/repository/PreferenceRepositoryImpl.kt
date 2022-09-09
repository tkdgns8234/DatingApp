package com.hoon.datingapp.data.repository

import com.hoon.datingapp.data.preference.PreferenceManager

internal class PreferenceRepositoryImpl(
    private val preferenceManager: PreferenceManager
) : PreferenceRepository {
    override fun putCurrentUserID(uid: String) {
        preferenceManager.putCurrentUserID(uid)
    }

    override fun getCurrentUserID(): String? {
        return preferenceManager.getCurrentUserID()
    }

    override fun removeCurrentUserID(){
        preferenceManager.removeCurrentUserID()
    }
}