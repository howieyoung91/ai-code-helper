package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.java.JavaDocCommentGenerator
import com.github.howieyoung91.aicodehelper.util.Plugin
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod
import com.jetbrains.rd.util.ThreadLocal

/**
 * @author Howie Young
 * @date 2023/03/11 17:04
 */
class CommentGenerate4MethodAction : AnAction() {
    private val threadLocal: ThreadLocal<PsiMethod> = ThreadLocal()

    override fun actionPerformed(e: AnActionEvent) {
        val method = threadLocal.get() ?: return
        threadLocal.remove()
        val project = e.project ?: return
        if (Plugin.isAvailable(project)) {
            val factory = PsiElementFactory.getInstance(project) ?: return
            JavaDocCommentGenerator.generate(
                Query {
                    prompt(method.text)
                },
                method,
                factory
            )
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        if (canView(e, threadLocal)) {
            e.presentation.isVisible = true
        }
    }
}
