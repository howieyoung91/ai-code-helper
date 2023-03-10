package com.github.howieyoung91.commentwriter.ai.chatgpt

class ChatGPT private constructor() {
    companion object {
        val config = ChatGPTConfig.instance
        val client = ChatGPTClient.instance
    }
}