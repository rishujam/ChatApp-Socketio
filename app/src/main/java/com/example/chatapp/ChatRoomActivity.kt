package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.data.models.ErrorState
import com.example.chatapp.databinding.ActivityChatRoomBinding
import com.example.chatapp.data.models.InitialData
import com.example.chatapp.data.models.Message
import com.example.chatapp.data.models.SendMessage
import com.example.chatapp.presentation.ChatViewModel
import com.example.chatapp.util.MessageType
import com.example.chatapp.util.Resource
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.io.IOException

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var binding: ActivityChatRoomBinding
    lateinit var userName: String
    lateinit var roomName: String
    val chatList: ArrayList<Message> = arrayListOf()
    lateinit var chatRoomAdapter: ChatRoomAdapter
    private val viewModel: ChatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.send.setOnClickListener(this)

        binding.leave.setOnClickListener(this)

        try {
            userName = intent.getStringExtra("userName")!!
            roomName = intent.getStringExtra("roomName")!!
        }catch (e:Exception){
            Log.d("ChatRoomActivity", "No args")
        }

        chatRoomAdapter = ChatRoomAdapter(this, chatList)
        binding.recyclerView.adapter = chatRoomAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.connectSocketAndSocketEvents(userName, roomName)

        viewModel.connectEvents.observe(this, Observer {
            when(it.getContentIfNotHandled()){
                is Resource.Success<ErrorState> -> {
                    val data = it.peekContent().data
                    if(data!=null){
                        lifecycleScope.launchWhenStarted {
                            var messages = emptyList<Message>()
//                            binding.recyclerView.withModels {
//                                messages.forEach { message ->
//                                    messageItem {
//                                        id(message.uid)
//                                        message(message)
//                                        isOutGoing(message.username == username)
//                                    }
//                                }
//                            }

//                            viewModel.chats(MessageType.CHAT_MINE.index,roomName).collect {
//                                messages = it
//                                binding.recyclerView.requestModelBuild()
//                            }
                        }.invokeOnCompletion {
                            Toast.makeText(this, "Start chatting", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Error<ErrorState> -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading<*> -> {
                }
                else -> {}
            }
        })
    }

    private fun sendMessage() {
        val content = binding.editText.text.toString()
        if(content.isNotEmpty()){
            viewModel.sendMessage(content, userName, roomName)
        }else{
            Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
        }
        viewModel.messageState.observe(this, Observer {
            when(it.getContentIfNotHandled()){
                is Resource.Success<ErrorState> -> {
                    val message = Message(userName, content, roomName, MessageType.CHAT_MINE.index)
                    addItemToRecyclerView(message)
                }
                is Resource.Error<ErrorState> ->{
                    Toast.makeText(this, "Message not sent", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading<*>->{}
                else ->{}
            }
        })
        val message = Message(userName, content, roomName, MessageType.CHAT_MINE.index)
        addItemToRecyclerView(message)
    }

    private fun addItemToRecyclerView(message: Message) {
        runOnUiThread {
            chatList.add(message)
            chatRoomAdapter.notifyItemInserted(chatList.size)
            binding.editText.setText("")
            binding.recyclerView.scrollToPosition(chatList.size - 1) //move focus on last message
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.send -> sendMessage()
            R.id.leave -> onDestroy()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.close(userName, roomName)
        finish()
    }

}