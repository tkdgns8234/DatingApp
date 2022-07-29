package com.hoon.datingapp.util

class DBKey {
    companion object {
        const val DB_NAME = "DatingApp"
        const val USERS = "Users"

        const val USER_ID = "userID"
        const val USER_NAME = "userName"
        const val USER_IMAGE_URI = "imageURI"

        const val LIKED_BY = "likedBy"
        const val LIKE = "like"
        const val DIS_LIKE = "dislike"
        const val MATCH = "match"
    }
}

class Constants {
    companion object {
        const val FACEBOOK_INFO_EMAIL = "email"
        const val FACEBOOK_INFO_PUBLIC_PROFILE = "public_profile"
        const val INTENT_KEY_PROFILE_NAME = "profile_name"
        const val INTENT_KEY_PROFILE_IMAGE_URI = "profile_image_uri"
        const val FIREBASE_STORAGE_PATH_IMAGES = "profile/images"
    }
}