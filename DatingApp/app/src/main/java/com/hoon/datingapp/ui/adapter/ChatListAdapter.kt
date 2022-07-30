package com.hoon.datingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.databinding.ItemChatBinding
import com.hoon.datingapp.util.DBKey

class ChatListAdapter : ListAdapter<ChatRoom, ChatListAdapter.ViewHolder>(diffUtil) {
    private val usersDB = Firebase.database.reference.child(DBKey.DB_NAME).child(DBKey.USERS)

    inner class ViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRoom: ChatRoom) {
            getPartnerInfo(
                chatRoom.partnerID,
                completeHandler = { name, imageURI ->
                    binding.tvName.text = name
                    binding.tvLastMessage.text = chatRoom.lastMessage

                    Glide
                        .with(binding.root)
                        .load(imageURI)
                        .into(binding.imageView)
                })
        }

        private fun getPartnerInfo(uid: String, completeHandler: (String, String) -> Unit) {
            val userDB = usersDB.child(uid)
            userDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child(DBKey.USER_NAME).value.toString()
                    val imageURI = snapshot.child(DBKey.USER_IMAGE_URI).value.toString()

                    completeHandler(name, imageURI)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ChatRoom>() {
            override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                return oldItem == newItem
            }
        }
    }
}