package com.hoon.datingapp.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.ui.adapter.CardItemAdapter
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.databinding.FragmentLikeBinding
import com.hoon.datingapp.ui.view.LoginActivity
import com.hoon.datingapp.util.DBKey
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction

class LikeFragment : Fragment() {
    private var _binding: FragmentLikeBinding? = null // 메모리 leak 방지
    private val binding get() = _binding!! // null 체크 없이 binding 객체에 접근하기 위함

    private var auth = FirebaseAuth.getInstance()
    private lateinit var usersDB: DatabaseReference
    private val cardStackAdapter = CardItemAdapter()
    private val userProfiles = mutableListOf<UserProfile>()
    private val cardStackLayoutManager by lazy {
        CardStackLayoutManager(context, CardStackListener())
    }

    inner class CardStackListener : com.yuyakaido.android.cardstackview.CardStackListener {
        override fun onCardDragging(direction: Direction?, ratio: Float) {}
        override fun onCardSwiped(direction: Direction?) {
            when (direction) {
                Direction.Left -> disLike()
                Direction.Right -> like()
            }
        }

        override fun onCardRewound() {}
        override fun onCardCanceled() {}
        override fun onCardAppeared(view: View?, position: Int) {}
        override fun onCardDisappeared(view: View?, position: Int) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS)

        initCardStackView()
        getUnSelectedUsers()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initCardStackView() {
        binding.cardStackView.layoutManager = cardStackLayoutManager
        binding.cardStackView.adapter = cardStackAdapter
    }

    private fun getUnSelectedUsers() {
        usersDB.addChildEventListener(object : ChildEventListener {
            // 새로 추가된 유저 확인
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val currentUserID = getCurrentUserId()
                val newProfile = snapshot.getValue(UserProfile::class.java) ?: return
                val findResult = userProfiles.find { it.userID == newProfile.userID }

                if (findResult == null &&
                    newProfile.userID != currentUserID &&
                    // 내가 like, dislike 한 적 있는 상대인지 확인
                    snapshot.child(DBKey.LIKED_BY).child(DBKey.LIKE).hasChild(currentUserID)
                        .not() &&
                    snapshot.child(DBKey.LIKED_BY).child(DBKey.DIS_LIKE).hasChild(currentUserID)
                        .not()
                ) {
                    userProfiles.add(newProfile)
                    cardStackAdapter.submitList(userProfiles)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // 프로필이 변경된 유저가 있는경우 업데이트
                val newProfile = snapshot.getValue(UserProfile::class.java) ?: return
                val findResult =
                    userProfiles.find {
                        it.userID == newProfile.userID
                    }

                findResult?.let {
                    it.userName = newProfile.userID
                    it.imageURI = newProfile.imageURI
                }

                cardStackAdapter.submitList(userProfiles)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun like() {
        val otherUserProfile =
            userProfiles[cardStackLayoutManager.topPosition - 1] // 현재 display 되고 있는 view의 position 반환, 1부터 시작

        otherUserProfile.userName
        userProfiles[0].userName

        userProfiles.removeFirst() // like 했으니 더이상 표시하지 않는다.
        cardStackAdapter.submitList(userProfiles)

        userProfiles.count()

        // 상대방의 userID의 like에 나의 id를 저장
        usersDB.child(otherUserProfile.userID)
            .child(DBKey.LIKED_BY)
            .child(DBKey.LIKE)
            .child(getCurrentUserId())
            .setValue(true)

        //서로 like 하여 매칭되었는지 확인
        makeChatIfOtherUserLikeMe(otherUserProfile.userID)

        Toast.makeText(context, "${otherUserProfile.userName}님을 like 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun disLike() {
        val otherUserProfile =
            userProfiles[cardStackLayoutManager.topPosition - 1] // 현재 display 되고 있는 view의 position 반환, 1부터 시작
        userProfiles.removeFirst()
        cardStackAdapter.submitList(userProfiles)

        // 상대방의 userID의 dislike에 나의 id를 저장
        usersDB.child(otherUserProfile.userID)
            .child(DBKey.LIKED_BY)
            .child(DBKey.DIS_LIKE)
            .child(getCurrentUserId())
            .setValue(true)

        Toast.makeText(
            context, "" +
                    "${otherUserProfile.userName}님을 dis like 하셨습니다.", Toast.LENGTH_SHORT
        ).show()
    }

    private fun makeChatIfOtherUserLikeMe(otherUserId: String) {
        val myUserDB =
            usersDB.child(getCurrentUserId()).child(DBKey.LIKED_BY).child(DBKey.LIKE)
                .child(otherUserId)
        myUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == true) {
                    saveChatInfo(otherUserId, getCurrentUserId())
                    saveChatInfo(getCurrentUserId(), otherUserId)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveChatInfo(myId: String, partnerId: String) {
        var key = ""
        listOf(myId, partnerId).sorted().forEach { key += it }

        usersDB
            .child(myId)
            .child(DBKey.CHAT)
            .push()
            .setValue(ChatRoom(myId, partnerId, key, ""))
    }


    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(context, getString(R.string.status_not_login), Toast.LENGTH_SHORT).show()

            startActivity(
                Intent(context, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }

        return auth.currentUser!!.uid
    }


}