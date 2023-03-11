package com.github.howieyoung91.aicodehelper.ai.chatgpt

import com.github.howieyoung91.aicodehelper.ai.exception.AiException

class ChatGPT private constructor() {
    companion object {
        val config = ChatGPTConfig.instance
        val client = ChatGPTClient.instance
    }
}

data class ChatGPTRequest(var prompt: String = "") {
    var callback: ChatGPTCallback = ChatGPTCallback()

    fun prompt(prompt: String) {
        this.prompt = prompt;
    }

    fun callback(block: ChatGPTCallback.() -> Unit) {
        this.callback.block()
    }
}

fun ChatGPTRequest(init: ChatGPTRequest.() -> Unit): ChatGPTRequest {
    val response = ChatGPTRequest()
    response.init()
    return response
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

/**
 * 在 Client 收到响应之后的回调函数
 */
data class ChatGPTCallback(
    inline var onFail: AiException.() -> Unit = {
        this.printStackTrace()
    },
    inline var onResponse: ChatGPTResponse.() -> Unit = {},
)