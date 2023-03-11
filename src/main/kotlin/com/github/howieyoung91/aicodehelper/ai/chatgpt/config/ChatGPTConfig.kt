package com.github.howieyoung91.aicodehelper.ai.chatgpt.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github91.howieyoung91.aicodehelper.ChatGPTConfig",
    storages = [Storage("aicodehelper.xml")]
)
class ChatGPTConfig : PersistentStateComponent<ChatGPTConfig> {
    var apikey = ""
    var model = "text-davinci-003"
    var maxToken = 1000
    var temperature = 0.8

    override fun getState(): ChatGPTConfig {
        return this
    }

    override fun loadState(state: ChatGPTConfig) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: ChatGPTConfig by lazy {
            ApplicationManager.getApplication().getService(ChatGPTConfig::class.java)
        }
    }
}

fun resolveApiKey(apiKey: String): String = "Bearer $apiKey"
