/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.DefaultGeneratePoint
import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.edit.PolishTextGenerator
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElementFactory

/**
 * TODO
 * @author Howie Young
 * @date 2023/03/18 14:12
 */
class PolishCommentAction : VisibleAction<PsiComment>() {
    override val targetClass: Class<PsiComment> = PsiComment::class.java

    override fun actionPerformed(target: PsiComment, event: AnActionEvent) {
        val project = event.project!!
        val factory = PsiElementFactory.getInstance(project) ?: return

        var text = target.text
        if (text.length <= 2) {
            return
        }

        text = if (text[2] == '*') {
            text.substring(3, text.length - 2)
        }
        else if (text[1] == '*') {
            text.substring(2, text.length - 2)
        }
        else {
            text.substring(2)
        }

        PolishTextGenerator.generate(
            DefaultGeneratePoint(
                Query { prompt(text) },
                target,
                factory,
                project
            )
        )
    }
}
