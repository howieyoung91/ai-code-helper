/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.ui.dialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.dsl.builder.panel
import java.awt.BorderLayout
import java.awt.Dimension

/**
 * @author Howie Young
 * @date 2023/03/20 19:05
 */
class CustomizeTemplateAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = EditorFactory.getInstance()
            .createEditor(DocumentImpl(e.getData(CommonDataKeys.EDITOR)?.selectionModel?.selectedText ?: ""))

        val project = e.project!!
        dialog(project, {
            title = "Customize Template"
            isResizable = false
            isOKActionEnabled = true
        }) {
            panel {
                row {
                    cell(NonOpaquePanel(BorderLayout()).apply {
                        preferredSize = Dimension(600, 400)
                        add(editor.component)
                    })
                }.rowComment("Write you own template here please.")
            }.withPreferredHeight(400)
                .withPreferredWidth(400)
        }.show()
    }
}
