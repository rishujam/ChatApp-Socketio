package com.example.chatapp.data.repository

import com.example.chatapp.data.models.ErrorState
import com.example.chatapp.data.socket.ChatConn
import com.example.chatapp.util.Resource
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatConn: ChatConn
) {

    fun connectSocketAndSocketEvents(userName:String, roomName:String): Resource<ErrorState> {
        return try {
            chatConn.connectSocketAndSocketEvents(userName, roomName)
            Resource.Success(ErrorState(false))
        }catch (e:Exception){
            Resource.Error(e.message,ErrorState(true, e.message))
        }
    }


    fun close(userName: String, roomName: String) =
        chatConn.close(userName, roomName)

    fun chats(viewType: Int, roomName: String) =
        chatConn.chats(viewType, roomName)

    suspend fun sendMessage(message: String, userName: String, roomName: String): Resource<ErrorState> {
        return try {
            chatConn.sendMessage(message, userName, roomName)
            Resource.Success(ErrorState(false))
        }catch (e:Exception){
            Resource.Error(e.message,ErrorState(true))
        }

    }

}