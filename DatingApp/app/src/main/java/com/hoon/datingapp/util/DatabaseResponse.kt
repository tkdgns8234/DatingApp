package com.hoon.datingapp.util

sealed class DatabaseResponse {

    data class Success<T>(
        val result: T
    ) : DatabaseResponse()

    object Failed : DatabaseResponse()

}