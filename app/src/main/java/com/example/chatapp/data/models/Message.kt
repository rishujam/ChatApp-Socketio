package com.example.chatapp.data.models

data class Message(
    val userName:String,
    val messageContent: String,
    val roomName:String,
    var viewType: Int
)
