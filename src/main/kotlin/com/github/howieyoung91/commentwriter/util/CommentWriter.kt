package com.github.howieyoung91.commentwriter.util

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaDocumentedElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.javadoc.PsiDocComment

/**
 * @author Howie Young
 * @date 2023/03/09 17:50
 */
object CommentWriter {
    private val log = Logger.getInstance(CommentWriter::class.java)

    /**
     * 对某一个元素添加文档注释
     *
     * @param target  要添加注释的元素
     * @param comment 注释
     *
     * @author Howie Young
     */
    @JvmStatic
    fun writeJavadoc(target: PsiElement, comment: PsiDocComment) {
        try {
            val project = target.project
            WriteCommandAction.runWriteCommandAction(project) {
                if (target.containingFile != null && target is PsiJavaDocumentedElement) {
                    doWriteJavaDoc(target, comment)
                    reformat(project, target)
                }
            }
        }
        catch (throwable: Throwable) {
            log.error("Fail to write Java Doc. Cause by: ", throwable)
        }
    }

    private fun doWriteJavaDoc(elem: PsiJavaDocumentedElement, comment: PsiDocComment) {
        val docComment = elem.docComment
        if (docComment == null) {
            elem.node.addChild(comment.node, elem.firstChild.node)
        }
        else {
            docComment.replace(comment)
        }
    }

    /**
     * 格式化文档
     *
     * @author Howie Young
     */
    private fun reformat(project: Project, elem: PsiElement) {
        val javadocElement = elem.firstChild
        val startOffset = javadocElement.textOffset
        val endOffset = javadocElement.textOffset + javadocElement.text.length
        val manager = CodeStyleManager.getInstance(project) ?: return
        manager.reformatText(elem.containingFile, startOffset, endOffset + 1)
    }
}