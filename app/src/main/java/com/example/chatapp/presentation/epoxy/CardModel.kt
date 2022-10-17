package com.example.chatapp.presentation.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.chatapp.databinding.ItemChatUserBinding

abstract class CardModel: EpoxyModelWithHolder<CardModel.CardHolder>() {

    @field:EpoxyAttribute
    open var userName: CharSequence? = null

    @field:EpoxyAttribute
    open var messageData: CharSequence? = null

    override fun bind(holder: CardHolder) {
        holder.binding.apply {
            message.text = messageData
        }
    }

    override fun unbind(holder: CardHolder) { }

    class CardHolder : EpoxyHolder() {
        lateinit var binding: ItemChatUserBinding
            private set

        override fun bindView(itemView: View) {
            binding = ItemChatUserBinding.bind(itemView)
        }
    }
}