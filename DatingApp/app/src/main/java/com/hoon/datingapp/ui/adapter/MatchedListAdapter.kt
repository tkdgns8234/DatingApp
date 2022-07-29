package com.hoon.datingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.ItemMatchedListBinding

class MatchedListAdapter : ListAdapter<UserProfile, MatchedListAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemMatchedListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userProfile: UserProfile) {

            with(binding.imageView) {
                Glide
                    .with(binding.root)
                    .load(userProfile.imageURI)
                    .into(this)

                this.clipToOutline = true
            }

            binding.tvName.text = userProfile.userName
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemMatchedListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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