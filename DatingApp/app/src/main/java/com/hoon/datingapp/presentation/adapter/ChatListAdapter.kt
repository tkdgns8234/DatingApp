package com.hoon.datingapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoon.datingapp.data.model.ChatRoom
import com.hoon.datingapp.databinding.ItemChatListBinding


// onclick 등 콜백함수는 nullable 프로퍼티로 빼서 null 처리하면서 사용하는 방법도 좋을듯 싶다.
class ChatListAdapter(
    val onClickListener: (partnerID: String, key: String) -> Unit,
    val getUserProfileHandler: (userID: String, completeHandler: (name: String, imageURI: String) -> Unit) -> Unit,
) : ListAdapter<ChatRoom, ChatListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRoom: ChatRoom) = with(binding) {

            val completeHandler = { name: String, imageURI: String ->
                with(binding) {
                    tvName.text = name
                    tvLastMessage.text = chatRoom.lastMessage

                    Glide
                        .with(root)
                        .load(imageURI)
                        .into(imageView)
                    imageView.clipToOutline = true
                }
            }

            root.setOnClickListener { onClickListener(chatRoom.partnerID, chatRoom.key) }
            getUserProfileHandler(chatRoom.partnerID, completeHandler)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ViewHolder {
        val binding =
            ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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