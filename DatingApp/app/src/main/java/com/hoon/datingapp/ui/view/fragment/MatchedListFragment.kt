package com.hoon.datingapp.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.ui.adapter.MatchedListAdapter
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.CardItem
import com.hoon.datingapp.databinding.FragmentMatchedListBinding
import com.hoon.datingapp.ui.view.LoginActivity
import com.hoon.datingapp.util.DBKey

class MatchedListFragment : Fragment() {
    private var _binding: FragmentMatchedListBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private lateinit var usersDB: DatabaseReference
    private val adapter = MatchedListAdapter()
    private val cardItems = mutableListOf<CardItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchedListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME)
            .child(DBKey.USERS)

        initViews()
        getMatchedUsers()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initViews() {
        binding.recyclerViewMatchedList.adapter = adapter
        binding.recyclerViewMatchedList.layoutManager = LinearLayoutManager(context)
    }

    private fun getMatchedUsers() {
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
            Toast.makeText(context, getString(R.string.status_not_login), Toast.LENGTH_SHORT).show()

            startActivity(
                Intent(context, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }

        return auth.currentUser!!.uid
    }
}