package com.hoon.datingapp.data.db

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.data.db.DBKey.CHATS_KEY
import com.hoon.datingapp.data.db.DBKey.DB_NAME
import com.hoon.datingapp.data.db.DBKey.USERS_KEY
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.util.DBKey
import com.hoon.datingapp.util.DatabaseResponse
import kotlinx.coroutines.CompletableDeferred

class FirebaseRealtimeDB {

    private val usersDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_NAME).child(USERS_KEY)
    }

    private val chatsDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_NAME).child(CHATS_KEY)
    }

    // realtime db를 위한 callback 호출 callback 호출 된 후 함수 return 하기위해 deferred 객체 사용
    // 데이터베이스를 참조하다가 에러가 난 경우 error 상태를 return 하도록 DatabaseResult class 구현
    internal suspend fun checkIsNewProfile(uid: String): DatabaseResponse {
        var retVal = CompletableDeferred<DatabaseResponse>()

        val userDB = usersDB.child(uid)
        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 프로필이 이미 등록된 회원인 경우 true
                val isExist = snapshot.child(DBKey.USER_NAME).value != null
                retVal.complete(DatabaseResponse.Success(isExist))
            }

            override fun onCancelled(error: DatabaseError) {
                retVal.complete(DatabaseResponse.Failed)
            }
        })

        return retVal.await()
    }

    internal suspend fun getUserProfile(uid: String): DatabaseResponse {
        var retVal = CompletableDeferred<DatabaseResponse>()

        usersDB.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val model: UserProfile? = snapshot.getValue(UserProfile::class.java)
                retVal.complete(DatabaseResponse.Success(model))
            }

            override fun onCancelled(error: DatabaseError) {
                retVal.complete(DatabaseResponse.Failed)
            }
        })

        return retVal.await()
    }

    internal suspend fun updateUserProfile(uid: String, name: String, imageUri: Uri) {
        val userProfile = UserProfile(uid, name, imageUri.toString())
        usersDB.child(uid).setValue(userProfile)
    }
}