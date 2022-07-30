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
import com.hoon.datingapp.ui.adapter.LikeMeListAdapter
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.FragmentLikeMeListBinding
import com.hoon.datingapp.ui.view.LoginActivity
import com.hoon.datingapp.util.DBKey

class LikeMeListFragment : Fragment() {
    private var _binding: FragmentLikeMeListBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private lateinit var usersDB: DatabaseReference
    private val adapter = LikeMeListAdapter()
    private val UserProfiles = mutableListOf<UserProfile>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLikeMeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersDB = Firebase.database.reference.child(DBKey.DB_NAME)
            .child(DBKey.USERS)

        initViews()
        getUsersLikeMe()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initViews() {
        binding.rvLikeMeList.adapter = adapter
        binding.rvLikeMeList.layoutManager = LinearLayoutManager(context)
    }

    private fun getUsersLikeMe() {
        val likedDB = usersDB.child(getCurrentUserId()).child(DBKey.LIKED_BY).child(DBKey.LIKE)
        likedDB.addChildEventListener(object :ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 매칭된 uid가 null이 아닌경우
                if (!snapshot.key.isNullOrEmpty()) {
                    getUserLikeMe(snapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getUserLikeMe(uid: String) {
        val likedUserDB = usersDB.child(uid)
        likedUserDB.addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val likedUserProfile = snapshot.getValue(UserProfile::class.java)
                likedUserProfile ?: return

                UserProfiles.add(likedUserProfile)
                adapter.submitList(UserProfiles)
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