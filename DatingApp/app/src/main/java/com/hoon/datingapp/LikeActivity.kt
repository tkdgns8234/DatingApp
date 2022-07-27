package com.hoon.datingapp

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.databinding.ActivityLikeBinding
import com.hoon.datingapp.util.DBKey
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction

class LikeActivity : AppCompatActivity() {
    private val binding: ActivityLikeBinding by lazy {
        ActivityLikeBinding.inflate(layoutInflater)
    }
    private var auth = FirebaseAuth.getInstance()
    private lateinit var usersDB: DatabaseReference
    private val cardStackAdapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()
    private val cardStackLayoutManager by lazy {
        CardStackLayoutManager(this, CardStackListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME)
            .child(DBKey.USERS)

        getCurrentUser()
        initCardStackView()
        initButton()
    }

    private fun initButton() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            finish()
        }

        binding.btnMatchList.setOnClickListener {
            startActivity(Intent(this, MatchedListActivity::class.java))
        }
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

    private fun getCurrentUser() {
        val currentUserDB = usersDB.child(getCurrentUserId())
        // realtime db는 listener 를 통해 데이터를 가져옴
        // addListenerForSingleValueEvent 의 경우 한번 호출되고 콜백이 즉시 삭제됨
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(DBKey.USER_NAME).value == null) {
                    showNameInputDialog()
                }
                getUnSelectedUsers()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // user name을 입력받는 dialog
    private fun showNameInputDialog() {
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("이름을 입력해 주세요")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                if (editText.text.isEmpty()) {
                    Toast.makeText(this@LikeActivity, "이름을 다시 입력해주세요", Toast.LENGTH_SHORT).show()
                    showNameInputDialog() // positive 버튼 클릭 시 종료되기에 다시 open
                } else {
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    // user name 추가해서 db에 저장
    private fun saveUserName(name: String) {
        // 기존 데이터는 유지하고 데이터 추가하는방법이 없는거같네,, 아쉽네
        val uid = getCurrentUserId()
        val currentUserDB = usersDB.child(uid)
        val user = mutableMapOf<String, Any>()
        user[DBKey.USER_ID] = uid
        user[DBKey.USER_NAME] = name
        currentUserDB.updateChildren(user)
    }

    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, getString(R.string.status_not_login), Toast.LENGTH_SHORT).show()
            finish() // Mainactivity의 onstart 콜백이 호출되면서 다시 login activity로 이동
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
            this, "${card.name}님을 like 하셨습니다.", Toast.LENGTH_SHORT
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
            this, "" +
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