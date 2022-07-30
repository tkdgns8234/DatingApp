package com.hoon.datingapp.data.model

data class ChatRoom(
    val myID: String,
    val partnerID: String,
    val key: String,
    val lastMessage: String,
) {
    constructor() : this("", "", "", "")
}