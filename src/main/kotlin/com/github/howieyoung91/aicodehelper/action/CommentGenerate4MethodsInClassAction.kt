package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.java.JavaDocCommentGenerator
import com.github.howieyoung91.aicodehelper.util.PSI
import com.github.howieyoung91.aicodehelper.util.Plugin
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.util.PsiUtilCore

/**
 * @author Howie Young
 * @date 2023/03/09 17:18
 */
class CommentGenerate4MethodsInClassAction : AnAction() {
    private val threadLocal: ThreadLocal<PsiClass> = ThreadLocal()

    override fun actionPerformed(e: AnActionEvent) {
        val clazz = threadLocal.get() ?: return
        threadLocal.remove()
        val project = e.project ?: return
        if (Plugin.isAvailable(project)) {
            val factory = PsiElementFactory.getInstance(project) ?: return
            generate4Classes(arrayOf(clazz), factory)
        }
    }

    private fun generate4Classes(classes: Array<out PsiClass>, factory: PsiElementFactory) {
        classes.forEach { clazz ->
            clazz.methods.filterNotNull().forEach { method ->
                JavaDocCommentGenerator.generate(
                    Query {
                        prompt(method.text)
                    },
                    method,
                    factory
                )
            }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        if (canView(e, threadLocal)) {
            e.presentation.isVisible = true
        }
    }
}

inline fun <reified T> canView(e: AnActionEvent, threadLocal: ThreadLocal<T>): Boolean {
    val project = e.project ?: return false
    if (!Plugin.isAvailable(project)) {
        return false
    }

    val file = e.getData(CommonDataKeys.PSI_FILE) ?: return false
    val editor = e.getData(CommonDataKeys.EDITOR) ?: return false
    val startPos = editor.selectionModel.selectionStart
    val elem = PsiUtilCore.getElementAtOffset(file, startPos)
    val e = PSI.getParent<T>(elem) ?: return false
    threadLocal.set(e)
    return true
}

