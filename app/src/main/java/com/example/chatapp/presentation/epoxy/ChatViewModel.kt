package com.example.chatapp.presentation.epoxy

import android.view.ViewParent
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.chatapp.R

class ChatViewModel: EpoxyModelWithHolder<CardModel.CardHolder>() {

    override fun getDefaultLayout(): Int {
        return R.layout.item_chat_user
    }

    override fun createNewHolder(parent: ViewParent): CardModel.CardHolder {
        return CardModel.CardHolder()
    }
}