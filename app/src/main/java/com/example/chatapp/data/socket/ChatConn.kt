package com.example.chatapp.data.socket

import com.example.chatapp.data.models.ErrorState
import com.example.chatapp.data.models.InitialData
import com.example.chatapp.data.models.Message
import com.example.chatapp.data.models.SendMessage
import com.example.chatapp.util.MessageType
import com.example.chatapp.util.Resource
import com.google.gson.Gson
import io.socket.client.AckWithTimeout
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class ChatConn @Inject constructor(
    val mSocket: Socket
) {

    fun connectSocketAndSocketEvents(userName:String, roomName:String) {
        mSocket.connect()
        val gson = Gson()
        val onConnect = Emitter.Listener {
            val data = InitialData(userName, roomName)
            val jsonData = gson.toJson(data)
            mSocket.emit("subscribe", jsonData)
        }
        val onUpdateChat = Emitter.Listener {
            val chat: Message = gson.fromJson(it[0].toString(), Message::class.java)
            chat.viewType = MessageType.CHAT_PARTNER.index
        }
        val onUserLeft = Emitter.Listener {
            val leftUserName = it[0] as String
        }
        val onNewUser = Emitter.Listener {
            val name = it[0] as String
        }
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on("newUserToChatRoom", onNewUser)
        mSocket.on("updateChat", onUpdateChat)
        mSocket.on("userLeftChatRoom", onUserLeft)

    }

    fun close(userName: String, roomName: String) {
        val gson = Gson()
        val data = InitialData(userName, roomName)
        val jsonData = gson.toJson(data)
        mSocket.emit("unsubscribe", jsonData)
        mSocket.disconnect()
    }

    fun chats(viewType: Int, roomName: String): Flow<Message> = callbackFlow {
        mSocket.on("updateChat") { args ->
            val json = args[0] as JSONObject
            val message = Message(
                userName = json.getString("username"),
                messageContent = json.getString("message"),
                roomName = roomName,
                viewType = viewType
            )
            trySendBlocking(message)
        }
        awaitClose {
            mSocket.off("updateChat")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun sendMessage(message: String, userName: String, roomName: String) = suspendCancellableCoroutine {
        val gson = Gson()
        val sendData = SendMessage(userName, message, roomName)
        val jsonData = gson.toJson(sendData)
        mSocket.emit("newMessage", jsonData, object : AckWithTimeout(10000) {
            override fun onSuccess(vararg args: Any?) {
                it.resume(Unit) {}
            }
            override fun onTimeout() {
                it.cancel(TimeoutException())
            }
        })
    }
}