package com.hoon.datingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.databinding.ActivityMatchedListBinding
import com.hoon.datingapp.util.DBKey

class MatchedListActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var usersDB: DatabaseReference
    private val adapter = MatchedListAdapter()
    private val cardItems = mutableListOf<CardItem>()

    private val binding by lazy {
        ActivityMatchedListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME)
            .child(DBKey.USERS)

        initViews()
        getMatchedList()
    }

    private fun initViews() {
        binding.recyclerViewMatchedList.adapter = adapter
        binding.recyclerViewMatchedList.layoutManager = LinearLayoutManager(this)
    }

    private fun getMatchedList() {
        val matchedDB = usersDB.child(getCurrentUserId()).child(DBKey.LIKED_BY).child(DBKey.MATCH)
        matchedDB.addChildEventListener(object :ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (!snapshot.key.isNullOrEmpty()) {
                    getMatchedUser(snapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getMatchedUser(uid: String) {
        val userNameDB = usersDB.child(uid).child(DBKey.USER_NAME)
        userNameDB.addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    val name = snapshot.value.toString()
                    cardItems.add(CardItem(uid, name))
                    adapter.submitList(cardItems)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, getString(R.string.status_not_login), Toast.LENGTH_SHORT).show()
            finish() // Mainactivity의 onstart 콜백이 호출되면서 다시 login activity로 이동
        }

        return auth.currentUser!!.uid
    }
}