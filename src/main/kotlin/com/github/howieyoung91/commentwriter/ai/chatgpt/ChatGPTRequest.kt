package com.github.howieyoung91.commentwriter.ai.chatgpt

import com.github.howieyoung91.commentwriter.ai.exception.AiException

// class ChatGPTSession {
//
// }

data class ChatGPTRequest(val prompt: String) {
    var callback: ChatGPTCallback = ChatGPTCallback()

    constructor(prompt: String, callback: ChatGPTCallback) : this(prompt) {
        this.callback = callback;
    }
}

class ChatGPTResponse {
    var content: String = ""

    fun content(content: String) {
        this.content = content;
    }
}

fun ChatGPTResponse(init: ChatGPTResponse.() -> Unit): ChatGPTResponse {
    val response = ChatGPTResponse()
    response.init()
    return response
}

data class ChatGPTCallback(
    inline val onFail: AiException.() -> Unit = { this.printStackTrace() },
    inline val onResponse: ChatGPTResponse.() -> Unit = {},
)