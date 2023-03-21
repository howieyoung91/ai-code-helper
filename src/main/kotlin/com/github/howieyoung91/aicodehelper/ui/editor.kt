/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.ui

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.impl.DocumentImpl
import javax.swing.JComponent

/**
 * @author Howie Young
 * @date 2023/03/21 00:29
 */
inline fun DocumentEditor(content: String = "", initEditor: Editor.() -> Unit = {}): JComponent {
    val editor = EditorFactory.getInstance()
        .createEditor(DocumentImpl(content))
    initEditor(editor)
    return editor.component
}

inline fun JComponent.DocumentEditor(content: String = "", initEditor: Editor.() -> Unit = {}): JComponent {
    val editor = EditorFactory.getInstance()
        .createEditor(DocumentImpl(content))
    initEditor(editor)
    this.add(editor.component)
    return editor.component
}