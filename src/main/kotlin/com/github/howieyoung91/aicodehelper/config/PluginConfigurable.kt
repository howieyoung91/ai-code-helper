/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.config

import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.ai.chatgpt.CustomChatGPTClient
import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfig
import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfigPanel
import com.github.howieyoung91.aicodehelper.generate.completion.JavaDocCommentGenerator
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * @author Howie Young
 * @date 2023/03/11 20:52
 */
class PluginConfigurable : Configurable {
    private var panel: ChatGPTConfigPanel? = null
    override fun createComponent(): JComponent {
        panel = ChatGPTConfigPanel()
        return panel!!.panel
    }

    override fun isModified(): Boolean {
        val chatgpt = ChatGPTConfig.instance
        return isModified(chatgpt)
    }

    private fun isModified(chatgpt: ChatGPTConfig): Boolean {
        val p = panel ?: return false
        return (
                (p.apikey != chatgpt.apikey)
                        or (p.model != chatgpt.model)
                        or ((call { p.maxToken.toInt() } ?: chatgpt.maxToken) != chatgpt.maxToken)
                        or ((call { p.temperature.toDouble() } ?: chatgpt.temperature) != chatgpt.temperature)
                        or ((call { p.retryCount.toInt() } ?: chatgpt.retryCount) != chatgpt.retryCount)
                        or (p.promptTemplate != chatgpt.promptTemplate)
                        or (p.outputTemplate != chatgpt.outputTemplate)
                        or (p.serverUrl != chatgpt.serverUrl)
                )
    }

    override fun apply() {
        val p = panel ?: return
        val chatgpt = ChatGPT.config
        chatgpt.apikey = p.apikey
        ChatGPT.client = lazy { CustomChatGPTClient(chatgpt.apikey) } // change a client
        chatgpt.model = p.model
        chatgpt.maxToken = call { p.maxToken.toInt() } ?: 1024
        chatgpt.temperature = call { p.temperature.toDouble() } ?: 0.5
        chatgpt.promptTemplate = p.promptTemplate
        chatgpt.outputTemplate = p.outputTemplate
        chatgpt.serverUrl = p.serverUrl
        chatgpt.retryCount = call { p.retryCount.toInt() } ?: 3
        JavaDocCommentGenerator.maxRetryCount(chatgpt.retryCount)
        ChatGPT.client = lazy { CustomChatGPTClient(chatgpt.apikey, chatgpt.serverUrl) } // change a client
    }

    override fun reset() {
        val p = panel ?: return
        val chatgpt = ChatGPT.config
        p.apikey = chatgpt.apikey
        p.model = chatgpt.model // chatgpt.model 有可能非法
        chatgpt.model = p.model // 写回 model，确保 model 一定合法
        p.maxToken = chatgpt.maxToken.toString()
        p.temperature = chatgpt.temperature.toString()
        p.retryCount = chatgpt.retryCount.toString()
        p.promptTemplate = chatgpt.promptTemplate
        p.outputTemplate = chatgpt.outputTemplate
        p.serverUrl = chatgpt.serverUrl
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return panel!!.preferredFocusedComponent
    }

    override fun disposeUIResources() {
        panel = null
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "AI Code Helper"
    }
}

private inline fun <T> call(block: () -> T): T? {
    return try {
        block()
    }
    catch (e: Exception) {
        null
    }
}