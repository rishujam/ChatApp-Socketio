package com.example.chatapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.models.ErrorState
import com.example.chatapp.data.models.Message
import com.example.chatapp.data.repository.ChatRepository
import com.example.chatapp.util.Resource
import com.example.chatapp.util.ViewModelEventWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel() {

    private val _messageState: MutableLiveData<ViewModelEventWrapper<Resource<ErrorState>>> = MutableLiveData()
    val messageState: LiveData<ViewModelEventWrapper<Resource<ErrorState>>>
        get() = _messageState

    private val _connectEvents: MutableLiveData<ViewModelEventWrapper<Resource<ErrorState>>> = MutableLiveData()
    val connectEvents: LiveData<ViewModelEventWrapper<Resource<ErrorState>>>
        get() = _connectEvents

    private val messages = ArrayList<Message>()

    fun connectSocketAndSocketEvents(userName:String, roomName:String) =viewModelScope.launch {
        _connectEvents.value = ViewModelEventWrapper(chatRepository.connectSocketAndSocketEvents(userName, roomName))
    }

    fun close(userName: String, roomName: String) =
        chatRepository.close(userName, roomName)

    fun chats(viewType: Int, roomName: String): Flow<List<Message>> {
        return flow {
            emit(messages)
            chatRepository.chats(viewType, roomName).onEach {
                messages.add(it)
            }.map { messages }
                .let { emitAll(it) }
        }
    }

    fun sendMessage(message: String, userName: String, roomName: String) = viewModelScope.launch {
        _messageState.value = ViewModelEventWrapper(chatRepository.sendMessage(message, userName, roomName))
    }

}