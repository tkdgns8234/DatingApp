package com.hoon.datingapp.data.model

data class ChatItem(
    val senderID: String,
    val message: String,
    val timeStamp: Long,
) {
    constructor(): this("", "", 0)
}