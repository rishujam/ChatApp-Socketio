package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.data.models.Message
import com.example.chatapp.util.MessageType

class ChatRoomAdapter(
    val context: Context,
    val chatList: ArrayList<Message>
): RecyclerView.Adapter<ChatRoomAdapter.ChatViewHolder>(){

    inner class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.username)
        val message = itemView.findViewById<TextView>(R.id.message)
        val text = itemView.findViewById<TextView>(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        var view: View? = null
        when(viewType){
            MessageType.CHAT_MINE.index -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_chat_user,parent,false)
            }
            MessageType.CHAT_PARTNER.index -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_chat_partner,parent,false)
            }
            MessageType.USER_JOIN.index -> {
                view = LayoutInflater.from(context).inflate(R.layout.chat_into_notification,parent,false)
            }
            MessageType.USER_LEAVE.index -> {
                view = LayoutInflater.from(context).inflate(R.layout.chat_into_notification,parent,false)
            }
        }
        return ChatViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val messageData = chatList[position]
        val userName = messageData.userName
        val content = messageData.messageContent
        val viewType = messageData.viewType

        when(viewType){
            MessageType.CHAT_MINE.index -> {
                holder.message.text = content
            }
            MessageType.CHAT_PARTNER.index -> {
                holder.userName.text = userName
                holder.message.text = content
            }
            MessageType.USER_JOIN.index -> {
                val text = "${userName} has entered the room"
                holder.text.text = text
            }
            MessageType.USER_LEAVE.index -> {
                val text = "${userName} has left the room"
                holder.text.text = text
            }

        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return chatList[position].viewType
    }
}