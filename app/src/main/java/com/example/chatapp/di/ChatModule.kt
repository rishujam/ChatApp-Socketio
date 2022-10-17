package com.example.chatapp.di

import com.example.chatapp.data.repository.ChatRepository
import com.example.chatapp.data.socket.ChatConn
import com.example.chatapp.util.Constants.CHAT_SOCKET_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideSocket():Socket{
        return IO.socket(CHAT_SOCKET_URL)
    }

    @Provides
    @Singleton
    fun provideChatConn(socket:Socket): ChatConn {
        return ChatConn(socket)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatConn: ChatConn): ChatRepository{
        return ChatRepository(chatConn)
    }
}