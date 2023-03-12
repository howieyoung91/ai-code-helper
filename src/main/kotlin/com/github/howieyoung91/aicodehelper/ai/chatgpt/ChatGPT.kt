package com.github.howieyoung91.aicodehelper.ai.chatgpt

import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfig
import com.github.howieyoung91.chatgpt.client.ChatGPTClient

class ChatGPT private constructor() {
    companion object {
        val config = ChatGPTConfig.instance
        val client = ChatGPTClient(config.apikey)
    }
}