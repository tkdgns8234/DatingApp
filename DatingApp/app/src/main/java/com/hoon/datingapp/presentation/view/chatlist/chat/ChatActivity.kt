package com.hoon.datingapp.presentation.view.chatlist.chat

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.databinding.ActivityChatBinding
import com.hoon.datingapp.presentation.adapter.ChatAdapter
import com.hoon.datingapp.presentation.view.login.LoginActivity
import com.hoon.datingapp.util.Constants
import com.hoon.datingapp.util.DBKey

class ChatActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }

    private val auth = FirebaseAuth.getInstance()
    private val messageList = mutableListOf<Message>()
    private val adapter = ChatAdapter(getCurrentUserId())
    private lateinit var chatDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        getPartnerImage()
        getChatHistory()
    }

    private fun getChatHistory() {
        val chatKey = intent.getStringExtra(Constants.INTENT_KEY_CHAT_KEY) ?: ""
        chatDB = Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.CHATS).child(chatKey)
        chatDB.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message ?: return

                messageList.add(message)
                adapter.submitList(messageList.toMutableList())
                binding.rvChat.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initViews() {
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(this)

        binding.btnBack.setOnClickListener { finish() }

        binding.etChat.addTextChangedListener {
            binding.btnSend.isEnabled = !binding.etChat.text.isNullOrEmpty()
        }

        binding.btnSend.isEnabled = false
        binding.btnSend.setOnClickListener {
            val message = Message(
                getCurrentUserId(),
                binding.etChat.text.toString(),
                System.currentTimeMillis()
            )
            chatDB.push().setValue(message)

            binding.etChat.text?.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etChat.windowToken, 0)
        }
    }

    private fun getPartnerImage() {
        val partnerID = intent.getStringExtra(Constants.INTENT_KEY_PARTNER_ID) ?: ""

        val partnerDB =
            Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS).child(partnerID)
        partnerDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageURI = snapshot.child(DBKey.USER_IMAGE_URI).value.toString()

                Glide
                    .with(this@ChatActivity)
                    .load(imageURI)
                    .into(binding.imageView)
                binding.imageView.clipToOutline = true
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getCurrentUserId(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, getString(R.string.status_not_login), Toast.LENGTH_SHORT).show()

            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }

        return auth.currentUser!!.uid
    }
}