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
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.FragmentChatListBinding
import com.hoon.datingapp.ui.adapter.ChatListAdapter
import com.hoon.datingapp.ui.adapter.LikeMeListAdapter
import com.hoon.datingapp.ui.view.LoginActivity
import com.hoon.datingapp.util.DBKey

class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private var auth = FirebaseAuth.getInstance()
    private lateinit var usersDB: DatabaseReference
    private val adapter = ChatListAdapter()
    private val chatRooms = mutableListOf<ChatRoom>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS)
        initViews()
        getUsersMatched()
    }

    private fun getUsersMatched() {
        usersDB.child()
    }

    private fun initViews() {
        binding.rvChatList.adapter = adapter
        binding.rvChatList.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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