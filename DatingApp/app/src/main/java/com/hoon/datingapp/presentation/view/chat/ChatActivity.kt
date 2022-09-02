package com.hoon.datingapp.presentation.view.chat

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.hoon.datingapp.R
import com.hoon.datingapp.data.model.Message
import com.hoon.datingapp.databinding.ActivityChatBinding
import com.hoon.datingapp.extensions.toast
import com.hoon.datingapp.presentation.adapter.ChatAdapter
import com.hoon.datingapp.presentation.view.BaseActivity
import com.hoon.datingapp.presentation.view.login.LoginActivity
import org.koin.android.viewmodel.ext.android.viewModel

internal class ChatActivity : BaseActivity<ChatViewModel, ActivityChatBinding>() {

    companion object {
        const val INTENT_KEY_CHAT_KEY = "chat_key"
        const val INTENT_KEY_PARTNER_ID = "partner_id"

        fun newIntent(context: Context, chatKey: String, partnerID: String) =
            Intent(context, ChatActivity::class.java).apply {
                putExtra(INTENT_KEY_CHAT_KEY, chatKey)
                putExtra(INTENT_KEY_PARTNER_ID, partnerID)
            }
    }

    override fun getViewBinding() =
        ActivityChatBinding.inflate(layoutInflater)

    override val viewModel by viewModel<ChatViewModel>()

    private val messageList = mutableListOf<Message>()
    private val adapter by lazy { ChatAdapter(getCurrentUserId()) }

    override fun observeData() {
        viewModel.chatStateLiveData.observe(this) {
            when (it) {
                is ChatState.UnInitialized -> {
                    initViews()
                    updatePartnerUserProfile()
                    traceChatHistory()
                }
                is ChatState.Success -> {
                    handleSuccessState(it)
                }
                is ChatState.Logout -> {
                    toast(getString(R.string.status_not_login))
                    startActivity(LoginActivity.newIntent(this))
                }
                is ChatState.Error -> {
                    handleErrorState(it.message)
                }
            }
        }
    }

    private fun handleErrorState(message: String) {
        toast(message)
        finish()
    }

    private fun handleSuccessState(state: ChatState.Success) {
        when (state) {
            is ChatState.Success.UpdatePartnerUserProfile -> {
                setPartnerUserImage(state.profile.imageURI)
            }
        }
    }

    private fun setPartnerUserImage(imageUri: String) {
        Glide
            .with(this@ChatActivity)
            .load(imageUri)
            .into(binding.imageView)
        binding.imageView.clipToOutline = true
    }

    private fun initViews() = with(binding) {
        rvChat.adapter = adapter
        rvChat.layoutManager = LinearLayoutManager(this@ChatActivity)

        btnBack.setOnClickListener { finish() }

        etChat.addTextChangedListener {
            btnSend.isEnabled = !binding.etChat.text.isNullOrEmpty()
        }

        btnSend.isEnabled = false
        btnSend.setOnClickListener {
            val chatKey = intent.getStringExtra(INTENT_KEY_CHAT_KEY) ?: ""
            val text = etChat.text.toString()
            viewModel.sendMessage(chatKey, text)

            etChat.text?.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(etChat.windowToken, 0)
        }
    }

    private fun traceChatHistory() {
        val chatKey = intent.getStringExtra(INTENT_KEY_CHAT_KEY) ?: ""

        viewModel.traceChatHistory(chatKey) { message ->
            messageList.add(message)
            adapter.submitList(messageList.toMutableList())
            binding.rvChat.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun updatePartnerUserProfile() {
        val partnerID = intent.getStringExtra(INTENT_KEY_PARTNER_ID) ?: ""
        viewModel.getPartnerUserProfile(partnerID)
    }

    private fun getCurrentUserId(): String {
        val uid = viewModel.getCurrentUserID()

        if (uid == null) {
            toast(getString(R.string.status_not_login))
            startActivity(LoginActivity.newIntent(this))
        }
        return uid!!
    }
}