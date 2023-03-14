/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.DefaultGeneratePoint
import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.java.JavaDocCommentGenerator
import com.github.howieyoung91.aicodehelper.util.Plugin
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod

/**
 * @author Howie Young
 * @date 2023/03/11 17:04
 */
class CommentGenerate4MethodAction : VisibleAction<PsiMethod>() {
    override val targetClass: Class<PsiMethod> = PsiMethod::class.java

    override fun actionPerformed(e: AnActionEvent) {
        val method = threadLocal.get() ?: return
        threadLocal.remove()
        val project = e.project ?: return
        if (Plugin.isAvailable(project)) {
            val factory = PsiElementFactory.getInstance(project) ?: return
            JavaDocCommentGenerator.generate(
                DefaultGeneratePoint(
                    method,
                    Query { prompt(method.text) },
                    factory
                )
            )
        }
    }
}
