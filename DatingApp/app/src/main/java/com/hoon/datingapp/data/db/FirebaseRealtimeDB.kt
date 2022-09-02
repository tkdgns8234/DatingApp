package com.hoon.datingapp.data.db

import android.net.Uri
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.data.db.DBKey.CHATS_KEY
import com.hoon.datingapp.data.db.DBKey.DB_NAME
import com.hoon.datingapp.data.db.DBKey.USERS_KEY
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.util.DatabaseResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.ext.scope

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
                val isNewProfile = snapshot.child(DBKey.USER_NAME).value == null
                retVal.complete(DatabaseResponse.Success(isNewProfile))
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

    internal fun updateUserProfile(uid: String, name: String, imageUri: Uri) {
        val userProfile = UserProfile(uid, name, imageUri.toString())
        usersDB.child(uid).setValue(userProfile)
    }

    internal fun sendMessage(chatKey: String, message: Message) {
        chatsDB.child(chatKey).push().setValue(message)
    }

    internal suspend fun getMatchedUsers(uid: String): DatabaseResponse {
        var retVal = CompletableDeferred<DatabaseResponse>()
        val chatList = mutableListOf<ChatRoom>()
        val userList = usersDB.child(uid).child(DBKey.CHAT)

        userList.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val chat = it.getValue(ChatRoom::class.java)
                    chat ?: return

                    chatList.add(chat)
                }
                retVal.complete(DatabaseResponse.Success(chatList))
            }

            override fun onCancelled(error: DatabaseError) {
                retVal.complete(DatabaseResponse.Failed)
            }
        })

        return retVal.await()
    }

    internal fun traceChatHistory(chatKey: String, callback: (Message) -> Unit) {
        Log.e("test", "chatKey=${chatKey}")
        chatsDB.child(chatKey).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message ?: return
                callback(message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    internal fun traceUsersLikeMe(uid: String, callback: (profile: UserProfile) -> Unit) {
        val likedMeListDB = usersDB.child(uid).child(DBKey.LIKED_BY).child(DBKey.LIKE)

        likedMeListDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // snapshot.key == 나를 like 하는 user id
                val userId = snapshot.key
                if (!userId.isNullOrEmpty()) {
                    getUserLikeMe(userId, callback)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
    }

    internal fun getUserLikeMe(uid: String, callback: (profile: UserProfile) -> Unit) {
        usersDB.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val model: UserProfile? = snapshot.getValue(UserProfile::class.java)
                model ?: return
                callback(model)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**
     * like 하거나 dislike 하지 않은 신규 유저를 trace 하는 함수
     * @param currentUid : 조회하고자하는 기준 id
     * @param newUserCallback : 신규 유저가 추가될 때 호출되는 callback
     * @param changedUserCallback : 유저 변경사항이 생겼을 때 호출되는 callback
     */
    internal fun traceNewUserAndChangedUser(
        currentUid: String,
        newUserCallback: (UserProfile) -> Unit,
        changedUserCallback: (UserProfile) -> Unit
    ) {
        usersDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newProfile = snapshot.getValue(UserProfile::class.java) ?: return

                if (newProfile.userID != currentUid &&
                    // 내가 like, dislike 한 적 있는 상대인지 확인
                    snapshot.child(DBKey.LIKED_BY).child(DBKey.LIKE).hasChild(currentUid).not() &&
                    snapshot.child(DBKey.LIKED_BY).child(DBKey.DIS_LIKE).hasChild(currentUid).not()
                ) {
                    newUserCallback(newProfile)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val newProfile = snapshot.getValue(UserProfile::class.java) ?: return
                changedUserCallback(newProfile)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    internal fun like(currentUserID: String, otherUserID: String) {
        // 상대방의 userID의 like에 나의 id를 저장
        usersDB.child(otherUserID)
            .child(DBKey.LIKED_BY)
            .child(DBKey.LIKE)
            .child(currentUserID)
            .setValue(true)
    }

    internal fun disLike(currentUserID: String, otherUserID: String) {
        // 상대방의 userID의 like에 나의 id를 저장
        usersDB.child(otherUserID)
            .child(DBKey.LIKED_BY)
            .child(DBKey.DIS_LIKE)
            .child(currentUserID)
            .setValue(true)
    }

    internal fun makeChatRoomIfLikeEachOther(currentUserID: String, otherUserId: String) {
        val db =
            usersDB.child(currentUserID).child(DBKey.LIKED_BY).child(DBKey.LIKE).child(otherUserId)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // otherUser 가 나를 like 했는지 확인
                if (snapshot.value == true) {
                    makeChatRoom(otherUserId, currentUserID)
                    makeChatRoom(currentUserID, otherUserId)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun makeChatRoom(currentUserID: String, otherUserId: String) {
        var key = ""
        listOf(currentUserID, otherUserId).sorted().forEach { key += it }

        usersDB
            .child(currentUserID)
            .child(DBKey.CHAT)
            .push()
            .setValue(ChatRoom(currentUserID, otherUserId, key, ""))
    }

}