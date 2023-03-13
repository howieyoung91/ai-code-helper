/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.ui

import com.intellij.ui.components.JBTextField
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class OnlyNumberTextField : JBTextField() {
    init {
        addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent?) {
                val ch = e?.keyChar ?: return
                if (ch < KeyEvent.VK_0.toChar() || ch > KeyEvent.VK_9.toChar()) {
                    e.consume()
                }
            }
        })
    }
}

class OnlyFloatTextField : JBTextField() {
    init {
        addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent?) {
                val ch = e?.keyChar ?: return
                if ((ch !in '0'..'9' && ch != '.')
                    or ("" == text && ch == '.')
                ) {
                    e.consume()
                }
                else if (text.contains(".")) {
                    if (ch == '.') {
                        e.consume()
                    }
                    else {
                        val dotIndex = text.indexOf('.')
                        if (text.substring(dotIndex + 1).length >= 2) {
                            e.consume()
                        }
                    }
                }
            }
        })
    }
}