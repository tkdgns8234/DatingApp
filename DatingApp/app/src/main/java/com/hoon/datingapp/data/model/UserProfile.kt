package com.hoon.datingapp.data.model

data class UserProfile(
    val userID: String,
    var userName: String,
    var imageURI: String
) {
    constructor() : this("", "", "")
}