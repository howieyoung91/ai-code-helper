package com.github.howieyoung91.aicodehelper.config

import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfig
import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfigPanel
import com.github.howieyoung91.chatgpt.client.ChatGPTClient
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
                        or (p.promptTemplate != chatgpt.promptTemplate)
                        or (p.outputTemplate != chatgpt.outputTemplate)
                )
    }

    override fun apply() {
        val p = panel ?: return
        val chatgpt = ChatGPT.config
        chatgpt.apikey = p.apikey
        ChatGPT.client = ChatGPTClient(chatgpt.apikey)
        chatgpt.model = p.model
        chatgpt.maxToken = call { p.maxToken.toInt() } ?: 1024
        chatgpt.temperature = call { p.temperature.toDouble() } ?: 0.5
        chatgpt.promptTemplate = p.promptTemplate
        chatgpt.outputTemplate = p.outputTemplate
    }

    override fun reset() {
        val p = panel ?: return
        val chatgpt = ChatGPT.config
        p.apikey = chatgpt.apikey
        p.model = chatgpt.model
        p.maxToken = chatgpt.maxToken.toString()
        p.temperature = chatgpt.temperature.toString()
        p.promptTemplate = chatgpt.promptTemplate
        p.outputTemplate = chatgpt.outputTemplate
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