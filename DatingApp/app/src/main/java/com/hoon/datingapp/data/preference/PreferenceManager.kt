package com.hoon.datingapp.data.preference

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(
    private val context: Context
) {

    companion object {
        const val PREFERENCE_NAME = "com-hoon-datingapp-pref"
        const val KEY_ID_CURRENT_USER = "UID"
    }

    private fun getSharedPreference(): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    private val pref by lazy { getSharedPreference() }

    private val editor by lazy { pref.edit() }


    /*
     *  firebase의 user id 저장
     *  - 참고 -
     *  apply: 비동기식
     *  commit: 동기식 (호출된 thread block)
     */
    fun putCurrentUserID(uid: String) {
        editor.putString(KEY_ID_CURRENT_USER, uid)
        editor.apply()
    }

    fun getCurrentUserID(): String? {
        return pref.getString(KEY_ID_CURRENT_USER, null)
    }

    fun removeCurrentUserID() {
        editor.remove(KEY_ID_CURRENT_USER)
        editor.apply()
    }
}