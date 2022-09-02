package com.hoon.datingapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoon.datingapp.data.model.UserProfile
import com.hoon.datingapp.databinding.ItemLikeMeBinding
import kotlinx.coroutines.withTimeout

class LikeMeListAdapter : ListAdapter<UserProfile, LikeMeListAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemLikeMeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userProfile: UserProfile) = with(binding) {

            Glide
                .with(binding.root)
                .load(userProfile.imageURI)
                .into(imageView)

            imageView.clipToOutline = true
            tvName.text = userProfile.userName
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemLikeMeBinding.inflate(
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