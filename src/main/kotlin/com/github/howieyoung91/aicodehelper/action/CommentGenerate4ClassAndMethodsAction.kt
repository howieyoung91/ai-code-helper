/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.DefaultGeneratePoint
import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.java.JavaDocCommentGenerator
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory

/**
 * @author Howie Young
 * @date 2023/03/14 15:30
 */
class CommentGenerate4ClassAndMethodsAction : VisibleAction<PsiClass>() {
    override val targetClass = PsiClass::class.java
    override fun actionPerformed(target: PsiClass, event: AnActionEvent) {
        val project = event.project!!
        val factory = PsiElementFactory.getInstance(project) ?: return
        JavaDocCommentGenerator.generate(
            DefaultGeneratePoint(
                Query { prompt(target.text) },
                target,
                factory,
                project
            )
        )
        target.methods.filterNotNull().forEach { method ->
            JavaDocCommentGenerator.generate(
                DefaultGeneratePoint(
                    Query { prompt(method.text) },
                    method,
                    factory,
                    project
                )
            )
        }
    }
}