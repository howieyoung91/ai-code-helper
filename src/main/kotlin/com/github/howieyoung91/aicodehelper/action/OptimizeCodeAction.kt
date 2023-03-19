/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.SelectionPoint
import com.github.howieyoung91.aicodehelper.generate.edit.OptimizedCodeGenerator
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor

/**
 * @author Howie Young
 * @date 2023/03/18 14:12
 */
class OptimizeCodeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
        OptimizedCodeGenerator.generate(SelectionPoint(Query(""), editor.selectionModel))
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
        if (editor.selectionModel.hasSelection()) {
            e.presentation.isVisible = true
        }
    }
}