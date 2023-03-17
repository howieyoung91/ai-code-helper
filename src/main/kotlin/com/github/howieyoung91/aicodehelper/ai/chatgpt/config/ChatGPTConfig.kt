/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.ai.chatgpt.config

import com.github.howieyoung91.aicodehelper.config.OUTPUT_PLACEHOLDER
import com.github.howieyoung91.aicodehelper.config.PROMPT_PLACEHOLDER
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

const val CHATGPT_API_URL = "https://api.openai.com"

val COMPLETION_MODELS = arrayOf(
    "text-davinci-003", // default
    "babbage", "davinci", "text-davinci-001", "ada", "curie-instruct-beta", "code-cushman-001",
    "text-ada-001", "text-curie-001", "davinci-instruct-beta", "text-davinci-002",
    "text-babbage-001", "code-davinci-002", "curie"
)

val COMPLETION_MODELS_MAP = object : HashMap<String, Int>() {
    init {
        COMPLETION_MODELS.withIndex().forEach { (i, model) ->
            put(model, i)
        }
    }
}


@State(
    name = "com.github91.howieyoung91.aicodehelper.ChatGPTConfig",
    storages = [Storage("aicodehelper.xml")]
)
class ChatGPTConfig : PersistentStateComponent<ChatGPTConfig> {
    var apikey = ""
    var model = COMPLETION_MODELS[0]
    var maxToken = 1024
    var temperature = 0.5
    var promptTemplate: String = "What does following code means?\n $PROMPT_PLACEHOLDER"
    var outputTemplate: String = OUTPUT_PLACEHOLDER
    var serverUrl: String = CHATGPT_API_URL
    var retryCount: Int = 3

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
