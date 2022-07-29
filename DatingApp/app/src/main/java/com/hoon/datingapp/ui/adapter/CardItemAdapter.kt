package com.hoon.datingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.ItemCardListBinding

class CardItemAdapter : ListAdapter<UserProfile, CardItemAdapter.CardViewHolder>(diffUtil) {
    inner class CardViewHolder(val binding: ItemCardListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userProfile: UserProfile) {
            Glide
                .with(binding.root)
                .load(userProfile.imageURI.toUri())
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewHolder {
        return CardViewHolder(
            ItemCardListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<UserProfile>() {
            override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
                return oldItem == newItem
            }

        }
    }
}