package com.github.howieyoung91.aicodehelper.config

import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfig
import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfigPanel
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

    private fun isModified(chatgpt: ChatGPTConfig): Boolean = (
            (panel!!.apikey != chatgpt.apikey)
                    or (panel!!.model != chatgpt.model)
                    or ((call { panel!!.maxToken.toInt() } ?: chatgpt.maxToken) != chatgpt.maxToken)
                    or ((call { panel!!.temperature.toDouble() } ?: chatgpt.temperature) != chatgpt.temperature)
            )

    override fun apply() {
        val chatgpt = ChatGPTConfig.instance
        chatgpt.apikey = panel!!.apikey
        chatgpt.model = panel!!.model
        chatgpt.maxToken = call { panel!!.maxToken.toInt() } ?: 1000
        chatgpt.temperature = call { panel!!.temperature.toDouble() } ?: 0.8
    }

    override fun reset() {
        val chatgpt = ChatGPTConfig.instance
        panel!!.apikey = chatgpt.apikey
        panel!!.model = chatgpt.model
        panel!!.maxToken = chatgpt.maxToken.toString()
        panel!!.temperature = chatgpt.temperature.toString()
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