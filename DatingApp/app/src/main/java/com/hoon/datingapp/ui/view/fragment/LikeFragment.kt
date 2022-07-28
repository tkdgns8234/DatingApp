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
import com.hoon.datingapp.data.model.CardItem
import com.hoon.datingapp.ui.adapter.CardItemAdapter
import com.hoon.datingapp.R
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
    private val cardItems = mutableListOf<CardItem>()
    private val cardStackLayoutManager by lazy {
        CardStackLayoutManager(context, CardStackListener())
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

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME)
            .child(DBKey.USERS)

        getUnSelectedUsers()
        initCardStackView()
        binding.test.setOnClickListener { auth.signOut() }
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
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val currentUserID = getCurrentUserId()
                if (cardItems.contains(snapshot.child(DBKey.USER_ID).value).not() &&
                    snapshot.child(DBKey.USER_ID).value != currentUserID &&
                    snapshot.child(DBKey.LIKED_BY).child(DBKey.LIKE).hasChild(currentUserID).not() &&
                    snapshot.child(DBKey.LIKED_BY).child(DBKey.DIS_LIKE).hasChild(currentUserID).not()
                ) {
                    val userID = snapshot.child(DBKey.USER_ID).value.toString()
                    var name = getString(R.string.name_undecided)
                    if (snapshot.child(DBKey.USER_NAME).value != null) {
                        name = snapshot.child(DBKey.USER_NAME).value.toString()
                    }
                    val cardItem = CardItem(userID, name)
                    cardItems.add(cardItem)
                    cardStackAdapter.submitList(cardItems)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cardItems.find { cardItem ->
                    cardItem.userID == snapshot.key
                }?.let {
                    it.name = snapshot.child(DBKey.USER_NAME).value.toString()
                }
                cardStackAdapter.submitList(cardItems)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
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

    private fun like() {
        // cardStackLayoutManager.topPosition 은 항상 1이 됨, cardItems.removeFirst() 를 수행하기 때문
        val card =
            cardItems[cardStackLayoutManager.topPosition - 1] // 현재 display 되고 있는 view의 position 반환, 1부터 시작, 상수값 0으로 바꿔도 무방
        cardItems.removeFirst() // 현재 보유중인 data list도 제거해서 sync를 맞춘다.
        cardStackAdapter.submitList(cardItems)

        // 상대방의 userID의 like에 나의 id를 저장
        usersDB.child(card.userID)
            .child(DBKey.LIKED_BY)
            .child(DBKey.LIKE)
            .child(getCurrentUserId())
            .setValue(true)

        //서로 like 하여 매칭되었는지 확인
        saveMatchIfOtherUserLikeMe(card.userID)

        Toast.makeText(
            context, "${card.name}님을 like 하셨습니다.", Toast.LENGTH_SHORT
        ).show()
    }

    private fun saveMatchIfOtherUserLikeMe(otherUserId: String) {
        val myUserDB =
            usersDB.child(getCurrentUserId()).child(DBKey.LIKED_BY).child(DBKey.LIKE)
                .child(otherUserId)
        myUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == true) {
                    usersDB
                        .child(otherUserId)
                        .child(DBKey.LIKED_BY)
                        .child(DBKey.MATCH)
                        .child(getCurrentUserId())
                        .setValue(true)

                    usersDB
                        .child(getCurrentUserId())
                        .child(DBKey.LIKED_BY)
                        .child(DBKey.MATCH)
                        .child(otherUserId)
                        .setValue(true)
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun disLike() {
        // cardStackLayoutManager.topPosition 은 항상 1이 됨, cardItems.removeFirst() 를 수행하기 때문
        val card =
            cardItems[cardStackLayoutManager.topPosition - 1] // 현재 display 되고 있는 view의 position 반환, 1부터 시작
        cardItems.removeFirst()
        cardStackAdapter.submitList(cardItems)

        // 상대방의 userID의 like에 나의 id를 저장
        usersDB.child(card.userID)
            .child(DBKey.LIKED_BY)
            .child(DBKey.DIS_LIKE)
            .child(getCurrentUserId())
            .setValue(true)

        Toast.makeText(
            context, "" +
                    "${card.name}님을 dis like 하셨습니다.", Toast.LENGTH_SHORT
        ).show()
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
}