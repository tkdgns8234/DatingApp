package com.hoon.datingapp.data.model

data class Chat(
    val senderID: String,
    val message: String,
    val timeStamp: Long,
) {
    constructor(): this("", "", 0)
}