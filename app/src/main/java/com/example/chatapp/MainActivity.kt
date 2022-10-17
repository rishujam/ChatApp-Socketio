package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener{
            enterChatRoom()
        }
    }

    private fun enterChatRoom() {
        val userName = binding.userName.text.toString()
        val roomName = binding.roomname.text.toString()

        if(roomName.isNotBlank() && userName.isNotBlank()) {
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("roomName", roomName)
            startActivity(intent)
        }else{
            Toast.makeText(this, "Please fill the name and room name first.", Toast.LENGTH_SHORT).show()
        }
    }
}