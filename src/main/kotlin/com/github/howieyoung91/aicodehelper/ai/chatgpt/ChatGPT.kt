/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.ai.chatgpt

import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfig
import com.github.howieyoung91.chatgpt.client.ChatGPTClient

class ChatGPT private constructor() {
    companion object {
        var config = ChatGPTConfig.instance
        var client = ChatGPTClient(config.apikey)
    }
}