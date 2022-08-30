package com.hoon.datingapp.data.model

data class Message(
    val senderID: String,
    val message: String,
    val timeStamp: Long,
) {
    constructor(): this("", "", 0)
}