package com.github.howieyoung91.aicodehelper.ai.chatgpt

/**
 * ChatGPT 配置
 *
 * @author Howie Young
 * @date 2023/03/09 21:29
 */
class ChatGPTConfig private constructor(init: ChatGPTConfig.() -> Unit) {
    var apiKey = ""; private set

    // var url = ""; private set
    var model = "text-davinci-003"; private set
    var maxToken = 1000; private set
    var temperature = 0.8; private set

    init {
        this.init()
    }

    fun apiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    companion object {
        val instance by lazy {
            ChatGPTConfig {
                apiKey("")
            }
        }
    }
}
