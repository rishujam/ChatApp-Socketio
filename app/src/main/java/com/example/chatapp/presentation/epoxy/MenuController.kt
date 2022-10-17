package com.example.chatapp.presentation.epoxy

import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import com.example.chatapp.data.models.Message

class MenuController: EpoxyController(
    EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    EpoxyAsyncUtil.getAsyncBackgroundHandler()
) {

    private var _currentResult: List<Message>? = null

    override fun buildModels() {
        val result = _currentResult
    }

    fun submit(result: List<Message>) {
        _currentResult = result
        requestModelBuild()
    }
}