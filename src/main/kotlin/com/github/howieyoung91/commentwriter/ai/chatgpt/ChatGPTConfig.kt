package com.github.howieyoung91.commentwriter.ai.chatgpt

/**
 * ChatGPT 配置
 *
 * @author Howie Young
 * @date 2023/03/09 21:29
 */
class ChatGPTConfig private constructor(init: ChatGPTConfig.() -> Unit) {
    var apiKey = ""; private set
    var url = ""; private set

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
