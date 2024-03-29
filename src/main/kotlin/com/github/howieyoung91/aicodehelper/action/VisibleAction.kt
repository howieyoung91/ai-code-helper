/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.util.PSI
import com.github.howieyoung91.aicodehelper.util.Plugin
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore

/**
 * @author Howie Young
 * @date 2023/03/13 11:13
 */
abstract class VisibleAction<T : PsiElement> : AnAction() {
    protected abstract val targetClass: Class<T>
    private val threadLocal = ThreadLocal<T>()
    final override fun actionPerformed(e: AnActionEvent) {
        val target = threadLocal.get() ?: return
        threadLocal.remove()
        val project = e.project ?: return
        if (Plugin.isAvailable(project)) {
            actionPerformed(target, e)
        }
    }

    open fun actionPerformed(target: T, event: AnActionEvent) {}

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        val target = getTarget(e, targetClass)
        if (target != null) {
            e.presentation.isVisible = true
            threadLocal.set(target)
        }
    }

    private fun getTarget(e: AnActionEvent, clazz: Class<T>): T? {
        val project = e.project ?: return null
        if (!Plugin.isAvailable(project)) {
            return null
        }
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return null
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
        val startPos = editor.selectionModel.selectionStart
        val elem = PsiUtilCore.getElementAtOffset(file, startPos)
        return PSI.getParent(elem, clazz)
    }
}
