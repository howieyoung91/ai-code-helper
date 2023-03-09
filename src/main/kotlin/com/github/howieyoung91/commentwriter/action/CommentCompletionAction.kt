package com.github.howieyoung91.commentwriter.action

import com.github.howieyoung91.commentwriter.generate.Query
import com.github.howieyoung91.commentwriter.generate.java.JavaDocCommentGenerator
import com.github.howieyoung91.commentwriter.util.CommentWriter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiJavaFile

/**
 * @author Howie Young
 * @date 2023/03/09 17:18
 */
class CommentCompletionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return

        if (file is PsiJavaFile) {
            val project = e.project ?: return
            val factory = PsiElementFactory.getInstance(project)
            file.classes.forEach { clazz ->
                clazz.methods.filterNotNull().forEach { method ->
                    val comment = factory.createDocCommentFromText(generateComment(), method)
                    CommentWriter.writeJavadoc(method, comment)
                }
            }
        }
    }
}

private fun generateComment() = JavaDocCommentGenerator.generate(
    Query {
        text("/** TODO */")
    }
)